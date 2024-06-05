# spring-ai
* This is a POC that leverages the Spring AI libraries to demonstrate their capabilities through a REST API.
* Specifically, this repository accesses OpenAI as its AI provider. 

# Setup
* Running the server requires getting a few API keys to access the services. Those keys
should be exported as environment variables in [this file](https://github.com/jack-truong/spring-ai/blob/main/src/main/resources/application.properties).
  * OpenAI (https://platform.openai.com/api-keys)
    * `spring.ai.openai.api-key=${OPENAI_API_KEY}`
  * FMP (https://site.financialmodelingprep.com/developer/docs/dashboard)
    * `spring.ai.stock.api-key=${STOCK_API_KEY}`

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
  * Request an image based on the passed in prompt.  The image can be returned as a URL or b64_json.
  * Calls to this endpoint log the resultant prompt that OpenAI uses to create the image.
  
* ## StockController
  This is the controller that returns information specific to stocks from OpenAI. Because GPT-4o has a knowledge cutoff date of Oct 2023, it cannot
  provide real-time data. 

  However, Spring-AI lets you provide functions that can provide this real-time data as additional context for the AI model
  to answer questions.  This controller provides such a function to call out to an external stocks REST API to get recent historical
  stock data from which the AI model can draw conclusions.

  * Request a randomized list of stocks from OpenAI.
  * Request recent historical information on the performance of a particular stock over a given time period.
  * Request a "recommendation" from the AI model on what the best stock purchase would be based on the last X days.
  * Calls to OpenAI are logged to show how many prompt tokens they are consuming.
