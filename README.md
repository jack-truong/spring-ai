# spring-ai
* This is a POC that leverages the Spring AI libraries to demonstrate their capabilities through a REST API.
* Specifically, this repository accesses OpenAI as its AI provider. 

# Demo
* A graphical demonstration of the API can be found [here](https://github.com/jack-truong/spring-ai-frontend). 

# Setup
* Running the server requires getting a few API keys to access the services. Those keys
should be exported as environment variables in [this file](https://github.com/jack-truong/spring-ai/blob/main/src/main/resources/application.properties).
  * OpenAI (https://platform.openai.com/api-keys)
    * `spring.ai.openai.api-key=${OPENAI_API_KEY}`
  * FMP (https://site.financialmodelingprep.com/developer/docs/dashboard)
    * `spring.ai.stock.api-key=${STOCK_API_KEY}`

# Running
  * This project is dependent on connecting to a locally running Postgres instance.  There's a 
    gradle task called `launchPostgres` that can be run to build and deploy the prepopulated Chinook 
    database using Docker.
  * You can run the server in IntelliJ by running the AiApplication configuration.
  * You can run the server in a terminal by running `./gradlew bootRun`.
    * The `bootRun` task is dependent on the `launchPostgres` task, so it should automatically start
    a docker container with the pre-populated Chinook database if it isn't already running.

# Controllers
* ## ChatController
  This is the generic chat controller that lets you submit any string prompt to get an AI string response back from OpenAI.
  
  * This controller also has some convenience methods for getting randomized list of values (e.g. foods, environments, etc.) from OpenAI.
  * Calls to OpenAI are logged to show how many prompt tokens they are consuming.
* ## DogChatController
  This controller has the specific context of returning AI responses related to dogs.
  
  * Request a randomized list of dog breeds from OpenAI.
  * Request an enumeration of dog characteristics.
  * Request details based on dog breed and given characteristics from OpenAI.
  * Request an image prompt based on the dog breed passed in, along with other values (e.g. environment, activity, etc.) from OpenAI.
  * Calls to OpenAI are logged to show how many prompt tokens they are consuming.

* ## ImageController
  This interfaces with DALL·E 3, which is OpenAI's image generation model.  It can take arbitrary prompts
  and generate complex images based on the tokens used to describe the scene.
  * Request an image to be created based on the passed in prompt.  
    * The image can be returned as a URL or b64_json. 
    * If it's a URL, the URL will only be valid for a set amount of time before the image is deleted and becomes inaccessible.
    * You can use very wide open prompts such as "Make me an image of a boy with blue hair riding a unicycle while juggling yo-yos."
  * Request an image to be analyzed based on the prompt and passed in image.  
    * This uses GPT-4o's vision analysis capabilities.
    * You can ask it a wide variety of things about the provided picture.
      * "How many yellow toothbrushes are in this picture?"
      * "What kind of dinner can I make with the ingredients in this picture?"
      * "What is the code shown in this picture trying to calculate?"
  * Calls to this endpoint log the resultant prompt that OpenAI uses to create the image.
  
* ## StockController
  This is the controller that returns information specific to stocks from OpenAI. Because GPT-4o has a knowledge cutoff date of Oct 2023, it cannot
  provide real-time data. 

  However, Spring-AI lets you provide functions that can provide this real-time data as additional context for the AI model
  to answer questions.  This controller provides such a function to call out to an external stocks REST API to get recent historical
  stock data from which the AI model can draw conclusions.

  * Request a randomized list of stocks from OpenAI.
  * Request recent historical information on the performance of a particular stock over a given time period.
  * Request a "recommendation" from the AI model on what the best stock purchase would be based on the last X days from 
  the current date. Because GPT-4o does not have access to real-time data, this causes OpenAI to instruct the backend to make calls 
  to an external stocks REST API service to provide the information that chat model needs to continue to answer the question.
  * Calls to OpenAI are logged to show how many prompt tokens they are consuming.

* ## DbController
  This is the controller that returns information specific to the Chinook database. 

  This controller utilizes the fact that Spring-AI lets you provide functions that can provide this real-time data as additional context for the AI model
  to answer questions.  This controller provides such a function to call allow queries to be run against the Chinook database. This is just a demonstration that
  the OpenAI model can be given contextual information about the database (its schema) and produce a query that attempts to answer the question passed in.  This would
  not be a truly practical solution to pass in schema on every request, but it does show that if given enough contextual information OpenAI can produce results specific
  to your personalized data set.

  * Questions like "What is the most popular album and its songs?" and "Give me all of the customers and employees that live in TX and their addresses" can be asked.
  * The controller is specifically instructed to disallow any questions that would attempt to modify the database.  In other words, only read-only queries are allowed.
  * As an additional safeguard, the function provided also validates that the query passed in does not contain any disallowed keywords that would modify the database.
  * Calls to OpenAI are logged to show how many prompt tokens they are consuming.
