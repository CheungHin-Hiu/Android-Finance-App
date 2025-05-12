# Android-Finance-App Backend

## Project Overview

This is the backend server for the Android-Finance-App, built with FastAPI. It provides API endpoints for financial data processing, user authentication, and integration with external services like Yahoo Finance.

The backend runs on Uvicorn, an ASGI server, and connects to a MongoDB database for data persistence.

---

## Prerequisites

- Python 3.7 or higher
- Git (optional, for cloning the repo)
- Internet connection for installing dependencies



---

## Running the Backend Locally

Follow these steps to set up and run the backend server on your local machine:

### 1. Unzip the Backend Folder

Extract the backend folder from the project archive if it is zipped.

### 2. Open CMD/Terminal

Launch your command prompt (Windows) or terminal (macOS/Linux).

### 3. Set Up Python Environment and Install Dependencies

Make sure you have installed all required Python packages.

- Navigate to the backend folder: cd \Android-Finance-App\Backend

- (Recommended) Create and activate a virtual environment:

### 4. Intialize Virtual Environment
### 5. pip install -r requirements.txt

### 6. Start the Backend Server

Run the Uvicorn server with the following command:
uvicorn main:app --host 0.0.0.0 --port 8080

- This will start the FastAPI app and make it accessible on your local network at port 8080.

### 7. Access the API

Open your browser or API client (like Postman) and navigate to: http://localhost:8080 or Initalize the Frontend for full experience


---

## Required Python Packages

Your `requirements.txt` should include:
- fastapi[standard]
- uvicorn
- python-dotenv
- PyJWT
- bcrypt
- pydantic
- pymongo
- yfinance



---

## Environment Variables

Create a `.env` file in the backend folder and copy the information into it:
- MONGO_URL = mongodb+srv://hoyinmok640:N0SRGET8YpWbMlQQ@comp4521.p24jqb7.mongodb.net/?retryWrites=true&w=majority&appName=COMP4521
- JWT_SECRET_KEY = MIHcAgEBBEIB5JMSj4ee1biodeU4oHTc1GiMYJ3He+59vAj7ArpcBJhMnfnlCFo4izPlhoWCmiY+HXICTNuVhhDjMl1cUKs+Qq2gBwYFK4EEACOhgYkDgYYABAHyt1nOW3yhrEJOMeYmVI/dEs33EL410UniE7R0ZBbj/uGMHeIxUQ900JTMSbj7W6OIEFUWVv5KBalEKazhAIMDDACnhMNEA6DSnZNQmqabkocZSsf8TMPeJgEHQeHJ68FH/HX0Dk6DX2w26rmcypGvrsrq70l4uIZ0SNhoxA7pCjQ3XA==
- JWT_ALGORITHM = HS256





