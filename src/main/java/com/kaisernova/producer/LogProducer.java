package com.kaisernova.producer;


import java.util.logging.Logger;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;



public class LogProducer {
   @Produces
   public Logger createLogger(InjectionPoint injectionPoint) { 
      return Logger.getLogger( injectionPoint.getMember().getDeclaringClass().getName() );
   }
}
