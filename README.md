curl -v -H 'Content-Type: application/json' -X POST -d @payload.json http://localhost:8280/weatherdata



<?xml version="1.0" encoding="UTF-8"?>
<api xmlns="http://ws.apache.org/ns/synapse" name="weatherdata" context="/weatherdata">
   <resource methods="POST">
      <inSequence>
                <property name="FORCE_SC_ACCEPTED" value="true" scope="axis2" type="STRING"/>
                <property name="OUT_ONLY" value="true" scope="default" type="STRING"/>
                <property name="messageType" value="application/json" scope="axis2"/>
                <class name="org.entgra.iot.weatherdata.mediator.WeatherDataMediator"/>
      </inSequence>
   </resource>
</api>
