import asyncio
import os
import json
import yfinance as yf
from datetime import datetime, timezone


# Singular batch get
async def _get_yahoo_stock_data(symbol: str):
    try:
        ticker = yf.Ticker(symbol)
        data = ticker.history(period="1d", interval="1m")
        return data
    except Exception as e:
        print(f"Error fetching data for {symbol}: {e}")
        return None


# Concurrent batch get
async def _get_batch_yahoo_stock_data(symbols: list):
    tasks = [_get_yahoo_stock_data(symbol) for symbol in symbols]
    stock_results = await asyncio.gather(*tasks)
    labeled_results = {symbol: stock for symbol, stock in zip(symbols, stock_results)}

    final_results = {symbol: [] for symbol in symbols}
    for symbol, stock_data in labeled_results.items():
        if stock_data is not None:
            symbol_data = []
            for index, row in stock_data.iterrows():
                symbol_data.append(
                    {
                        "Date": str(index),
                        "Open": float(row["Open"]),
                        "High": float(row["High"]),
                        "Low": float(row["Low"]),
                        "Close": float(row["Close"]),
                        "Volume": float(row["Volume"]),
                    }
                )
                break
            final_results[symbol] = symbol_data
    return final_results


async def _get_yahoo_currency_rate(currencies: list = None):
    # default currencies to CNY, HKD, JPY, USD if not provided
    if currencies is None:
        currencies = ["CNY", "HKD", "JPY", "USD"]

    tickers = []
    for from_currency in currencies:
        for to_currency in currencies:
            if from_currency != to_currency:
                ticker = from_currency.upper() + to_currency.upper() + "=X"
                tickers.append(ticker)

    try:
        df = yf.download(tickers, period="1d", interval="1d")
        if df.empty:
            raise ValueError("No data returned for the given currencies.")

        conversion_rates = {}
        for ticker in tickers:
            conversion_rates[ticker] = df.iloc[0, df.columns.get_loc(("Close", ticker))]

        return conversion_rates

    except Exception as e:
        print(f"Error fetching data for currencies {currencies}: {e}")
        return None


async def get_finance_data(
    currencies: list = None, stocks: list = None, cryptos: list = None
):
    # default currencies, stocks, and cryptos if not provided
    if currencies is None:
        currencies = ["CNY", "HKD", "JPY", "USD"]
    if stocks is None:
        stocks = ["AAPL", "AMZN", "GOOG", "NVDA"]
    if cryptos is None:
        cryptos = ["BTC-USD", "DOGE-USD", "ETH-USD", "USDT-USD"]
    else:
        cryptos = [crypto + "-USD" for crypto in cryptos]

    cache_folder = "finance_data_cache"
    cache_dir = os.path.join(cache_folder, 'data.json')
    # create cache folder if it doesn't exist
    os.makedirs(cache_folder, exist_ok=True)

    # return cached data if not expired
    if os.path.exists(cache_dir):
        with open(cache_dir, 'r') as f:
            cached_data = json.load(f)
            cached_time = datetime.fromisoformat(cached_data["timeRetrieved"])
            if cached_time.date() == datetime.now(timezone.utc).date():
                return cached_data

    # fetch new data from yfinace
    print("fetch data from yfinance")
    currency_response = await _get_yahoo_currency_rate(currencies)
    stock_response = await _get_batch_yahoo_stock_data(stocks)
    crypto_response = await _get_batch_yahoo_stock_data(cryptos)

    result = {
        "timeRetrieved": str(datetime.now(timezone.utc)),
        "currency": currency_response,
        "stock": stock_response,
        "crypto": crypto_response,
    }

    # save data to cache
    with open(cache_dir, 'w') as f:
        json.dump(result, f)

    return result
