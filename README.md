# flink-using-springDI
An example project for using Spring as a dependency Injection framework for Flink Jobs
This is based on [flink-spring](https://github.com/getindata/flink-spring) library.

# Modules
## BusinessLogic
A "core" module containing classes related to core business logic. Module than has no dependency on Flink
and in general can be used by a standard microservice application and Flink pipeline.

## FlinkPipeline
This is a Flink sandbox job that uses `flink-spring` library to set up Flink operators
and business code classes. This Job "process" `Order` objects using business logic provided by `BusinessLogic` module.

## SpringBootBusinessApp
This is a SpringBoot application that imitates a processing pipeline. This application "process"
`Order` objects using business logic provided by `BusinessLogic` module.
