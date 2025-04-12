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

