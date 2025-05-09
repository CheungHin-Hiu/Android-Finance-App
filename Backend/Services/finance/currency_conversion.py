from services.finance.finance_data_scraper import get_finance_data


async def currency_conversion(from_currency: str, to_currency: str, amount: float) -> float:
    # get currency rate
    finance_data = await get_finance_data()
    conversion_rates = finance_data["currency"]

    # convert amount to target currency
    conversion_rate = conversion_rates[f"{from_currency}{to_currency}=X"]   
    converted_amount = amount * conversion_rate

    return converted_amount


async def items_currency_conversion(items: list[dict], to_currency: str) -> list[dict]:
    # get currency rate
    finance_data = await get_finance_data()
    conversion_rates = finance_data["currency"]

    # append converted amount to each item
    for item in items:
        if item["currency"] != to_currency:
            # convert amount to target currency
            conversion_rate = conversion_rates[f"{item['currency']}{to_currency}=X"]
            item["converted_amount"] = item["amount"] * conversion_rate
        else:
            item["converted_amount"] = item["amount"]
        item["converted_currency"] = to_currency

    return items
