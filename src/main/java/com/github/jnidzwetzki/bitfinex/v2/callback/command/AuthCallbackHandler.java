/*******************************************************************************
 *
 *    Copyright (C) 2015-2018 Jan Kristof Nidzwetzki
 *  
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License. 
 *    
 *******************************************************************************/
package com.github.jnidzwetzki.bitfinex.v2.callback.command;

import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.ConnectionCapabilities;

public class AuthCallbackHandler implements CommandCallbackHandler {

	/**
	 * The Logger
	 */
	final static Logger logger = LoggerFactory.getLogger(AuthCallbackHandler.class);
	
	@Override
	public void handleChannelData(final BitfinexApiBroker bitfinexApiBroker, final JSONObject jsonObject) 
			throws APIException {

		final String status = jsonObject.getString("status");
		
		logger.info("Authentification callback state {}", status);
		
		if(status.equals("OK")) {
			bitfinexApiBroker.setAuthenticated(true);
			final ConnectionCapabilities capabilities = new ConnectionCapabilities(jsonObject);
			bitfinexApiBroker.setCapabilities(capabilities);
		} else {
			bitfinexApiBroker.setAuthenticated(false);
			logger.error("Unable to authenticate: {}", jsonObject.toString());
		}
		
		final CountDownLatch connectionReadyLatch = bitfinexApiBroker.getConnectionReadyLatch();
		
		if(connectionReadyLatch != null) {
			connectionReadyLatch.countDown();
		}
	}

}
