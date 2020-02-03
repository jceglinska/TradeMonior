The application consists 3 microservices:

TradeMonitor - monitors specific destination for added new .json files (every 15 seconds), reads them and creates Trade object form the .json data. 
Then Trade objects are transformed into bytes (serialization) and send to Kafka topic.

ConsumerService1 - every 60 seconds checks Kafka topic for new data. If something appears, data are deserialized and outputted to .json file to the new localization (specified in property jsonFiles.directory).

ConsumerService2 - same as above but checks every 5 minutes and writes data to different localization.

1. Project requirements
    -Java 8
    -Kafka
    -Maven
    -Git
    
2. How to run

    -download services using git clone, f.e. https://github.com/as1909/TradeMonior.git
    
    -run kafka & zookeeper
    
    -create new directory for sample .json data
    
    -in project TradeMonitor in config.properties specify the path to those files (jsonFiles.directory)
   
    -run project with main method src\main\java\monitor\trade\TradeMonitor.java
    
    To receive data form Kafka
    
    -specify destianation for .json files (ConsumerService1/ConsumerService2 -> config.properties -> jsonFiles.directory)
    -run using main method src\main\java\consumer\Consumer.java
    