# lunch-suggestion

## Available endpoints:
  ### cityName = Boston or Zagreb
  ### localhost:8080/{cityName}
  ### result is link to file on google storage with restaurant name and current weather description
  
  ### Map Table for Restaurant:
  
| GoogleAPI Places | Restaurant                |
|------------------|---------------------------|
| name             | name                      |
| rating           | rating                    |
| Weather          | see Weather mapping table |
|                  |                           |

  ### Map Table for Weather
  
  | OpenWeatherAPI      | Weather     |
|---------------------|-------------|
| main                | title       |
| weather.description | description |
| main.temp           | temperature |
|                     |             |
