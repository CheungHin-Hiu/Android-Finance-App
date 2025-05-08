import asyncio
import yfinance as yf

# Singular batch get
async def _get_yahoo_stock_data(symbol: str):
    try:
        ticker = yf.Ticker(symbol)
        data = ticker.history(period="2d", interval="1m")
        return data
    except Exception as e:
        print(f"Error fetching data for {symbol}: {e}")
        return None

# Concurrent batch get
async def get_batch_yahoo_stock_data(symbols: list):
    tasks = [_get_yahoo_stock_data(symbol) for symbol in symbols]
    stock_results = await asyncio.gather(*tasks)
    labeled_results = {symbol: stock for symbol, stock in zip(symbols, stock_results)}

    final_results = {symbol: [] for symbol in symbols}
    for symbol, stock_data in labeled_results.items():
        if stock_data is not None:
            symbol_data = []  
            for index, row in stock_data.iterrows():
                symbol_data.append({
                    'Date': index,
                    'Open': float(row['Open']),
                    'High': float(row['High']),
                    'Low': float(row['Low']),
                    'Close': float(row['Close']),
                    'Volume': float(row['Volume'])
                })
                break
            final_results[symbol] = symbol_data  
    return final_results

async def get_yahoo_currency_rate(currencies: list = None):
    # default currencies to CNY, HKD, JPY, USD if not provided
    if currencies is None:
        currencies = ['CNY', 'HKD', 'JPY', 'USD']

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
            conversion_rates[ticker] = df.iloc[0, df.columns.get_loc(('Close', ticker))]
        
        return conversion_rates
    
    except Exception as e:
        print(f"Error fetching data for currencies {currencies}: {e}")
        return None