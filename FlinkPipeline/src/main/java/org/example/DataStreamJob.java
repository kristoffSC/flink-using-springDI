/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example;

import com.getindata.fink.spring.context.ContextRegistry;
import java.util.Properties;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.example.internal.CheckpointCountingSource;
import org.example.internal.CheckpointCountingSource.EventProducer;
import org.example.internal.FlinkBusinessLogic;
import org.example.model.Order;
import org.example.model.SessionizeOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Skeleton for a Flink DataStream Job.
 *
 * <p>For a tutorial how to write a Flink application, check the
 * tutorials and examples on the <a href="https://flink.apache.org">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class DataStreamJob {

	@Autowired
	private EventProducer<Order> eventProducer;

	@Autowired
	private SinkFunction<SessionizeOrder> sink;

	@Autowired
	private ProcessFunction<Order, SessionizeOrder> businessLogic;

	public static void main(String[] args) throws Exception {
		// Add Job argument to System arguments, so we can tell Spring via job arguments which bean
		// should be loaded. This is done by @ConditionalOnProperty
		ParameterTool parameterTool = ParameterTool.fromArgs(args);
		Properties argProperties = parameterTool.getProperties();
		System.getProperties().putAll(argProperties);

		new ContextRegistry()
			.autowiredBean(new DataStreamJob(), "org.example.config")
			.run(parameterTool);
	}

	private void run(ParameterTool parameterTool) throws Exception {
		StreamExecutionEnvironment env = createStreamEnv();
		env.getConfig().setGlobalJobParameters(parameterTool);
		env.addSource(new CheckpointCountingSource<>(10, 20, eventProducer))
			.setParallelism(1)
			.process(businessLogic)
			.setParallelism(2)
			.addSink(sink)
			.setParallelism(2);

		env.execute("Flink Job Powered By Spring DI.");
	}

	private static StreamExecutionEnvironment createStreamEnv() {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.getConfig().setRestartStrategy(RestartStrategies.noRestart());
		env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
		env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE);
		return env;
	}
}
