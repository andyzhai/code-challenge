package com.livebarn.sushishop.backend;

import reactor.core.publisher.Sinks;

public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 Sinks.Many<String> taskSink = Sinks.many().unicast().onBackpressureBuffer();
	}

}
