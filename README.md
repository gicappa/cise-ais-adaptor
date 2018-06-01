# CISE AIS Adaptor

## Overview 

The CISE AIS Adaptor is a software meant to read/consume AIS Tracks, 
process them translating them into a CISE Data Model complian message 
and to send PUSH messages to a pre-configured number of consumers.

The standard INPUT is an AIS NMEA transcription and the OUTPUT a number 
of CISE PUSH messages.

There will be two main AIS NMEA message type supported:
* Voyage Message Type 
* Positional Message Type

It should be possible to decide the ouput PUSH message frequency specifying:
- a number of AIS messages that will compose a single PUSH
- an amount of time after which a PUSH message will be sent

## General architecture
The software is composed by two modules: 

- ais-app
- ais-domain
- ais-tbs-source-adapter
- ais-server-example

### ais-app
Is the entry point that starts the message processing and it focus itself in 
translating the AIS Message from the library specific on to a project domain
specific so to be independent from the AIS library implementation.

The module will load the messages from a file or from a socket and will translate
it to the InternalAISMessage so to be processed by the domain module   

### ais-domain
Is the module that translates the InternalAISMessage into a CISE Message and 
that delivers the message to the CISE Gateway

### ais-server-example

TBD

### ais-tbs-source-adapter

TBD

## Message Mapping 

The library that has been used to read the AIS Messages is [tbsalling](https://github.com/tbsalling/aismessages/).

In this library messages type are mapped in an enum bound to the message type number.
Our initial implementation will treat only the following message types dropping 
all the messages with a different message type: 

* PositionReportClassAScheduled(1),
* PositionReportClassAAssignedSchedule(2),
* PositionReportClassAResponseToInterrogation(3),
* ShipAndVoyageRelatedData(5),

# Development notes

* There is a difference in latitude and longitude between the AIS and the
  CISE calculation. Here for simplicity it hasn't been taken into account.
  https://webgate.ec.europa.eu/CITnet/confluence/display/MAREX/AIS+Message+1%2C2%2C3
  
* The field Vessel.LocationRel.LocationQualitativeAccuracy has 
  no correspondence in David's table but in the AISMessage objects there is 
  a field named _positionAccuracy_ and it's a boolean that could correspond 
  to HIGH(true) and LOW(false). It must be checked if it's correct.
  
* _getMetadata()_ doesn't return anything but _dataFields().get("metadata")_
  does. Check if _getMetadata()_ it should be substituted with the latter 
  or not.

The following message types are the one supported byt the chosen AIS library but
our implementation only support four of them.  

* ShipAndVoyageRelatedData: _SUPPORTED_
* PositionReportClassAScheduled: _SUPPORTED_
* PositionReportClassAAssignedSchedule: _SUPPORTED_
* PositionReportClassAResponseToInterrogation: _SUPPORTED_
* BaseStationReport:
* AddressedBinaryMessage:
* BinaryAcknowledge:
* BinaryBroadcastMessage:
* StandardSARAircraftPositionReport:
* UTCAndDateInquiry:
* UTCAndDateResponse:
* AddressedSafetyRelatedMessage:
* SafetyRelatedAcknowledge:
* SafetyRelatedBroadcastMessage:
* Interrogation:
* AssignedModeCommand:
* GNSSBinaryBroadcastMessage:
* StandardClassBCSPositionReport:
* ExtendedClassBEquipmentPositionReport:
* DataLinkManagement:
* AidToNavigationReport:
* ChannelManagement:
* GroupAssignmentCommand:
* ClassBCSStaticDataReport:
* BinaryMessageSingleSlot:
* BinaryMessageMultipleSlot:
* LongRangeBroadcastMessage:



# TODO

* README
* ASCIIDOC
* CONTRIBUTING
* DISTRIBUTION
* ERROR CODE on EXCEPTIONS with LEGENDA
* Document how to handle certificates and where to put them.
* Create a test example 
* Create a support for environments (dev, test, prod)
* Adding a logger class in the domain
* Explaining in the documentation why there are messages of two types (AISMsg and AISMessage)


# TODO

When an option is not specified in the property file it fails with a null pointer exception.