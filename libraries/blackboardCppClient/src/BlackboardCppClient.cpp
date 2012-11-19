/**
 * @file BlackboardCppClient.cpp
 * @brief The cpp client for the blackboard
 * @date Created: 2012-11-12
 *
 * @author Dennis Koole
 *
 * @section LICENSE
 * License: newBSD
 *
 * Copyright © 2012, HU University of Applied Sciences Utrecht.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

#include <iostream>
#include <blackboardCppClient/BlackboardCppClient.h>
#include <Utilities/Utilities.h>
#include <memory>
#include <set>
#include <unistd.h>

/**
 * Constructor for the BlackboardCppClient
 *
 * @param hostname the name of the host where the mongo database can be found
 **/
BlackboardCppClient::BlackboardCppClient(const std::string &hostname, const std::string db, const std::string coll, BlackboardSubscriber *func): database(db), collection(coll), callback(func) {
  try {
    connection.connect(hostname);
    std::cout << "connected to database" << std::endl;
  } catch( const mongo::DBException &e ) {
    std::cout << "caught " << e.what() << std::endl;
  }
}

/**
 * Constructor for the BlackboardCppClient
 *
 * @param hostname the name of the host where the mongo database can be found
 **/
/*BlackboardCppClient::BlackboardCppClient(const std::string &hostname, int port, const std::string db, const std::string coll, CallbackFunc func): database(db), collection(coll), callback(func){
  try {
  	connection.connect(mongo::HostAndPort(hostname, port));
  	std::cout << "connected to database" << std::endl;
  } catch( const mongo::DBException &e ) {
    std::cout << "caught " << e.what() << std::endl;
  }	
}*/

/**
 * destructor for the BlackboardCppClient
 **/
BlackboardCppClient::~BlackboardCppClient() {
	readMessageThread->interrupt();
	delete readMessageThread;
}

/**
 * Set the name of the database to use
 * 
 * @param db the name of the database to use
 **/
void BlackboardCppClient::setDatabase(const std::string &db) {
	database = db;
}

/**
 * Set the name of the collection to use
 *
 * @param col the name of the collection to use
 **/
void BlackboardCppClient::setCollection(const std::string &col) {
	collection = col;
}

/**
 * Subscribe to a blackboard topic
 *
 * Subscribe to a blackboard topic. When the first subscription is added, 
 * a thread will be started to handle the messages from the blackboard 
 *
 * @param topic the name of the topic to subscribe to
 **/
void BlackboardCppClient::subscribe(const std::string &topic) {
	if(collection.empty()) {
		std::cerr << "Collection is empty" << std::endl;
	}
	if(database.empty()) {
		std::cerr << "Database is empty" << std::endl;
	}
	subscriptions.insert( std::pair<std::string, mongo::BSONObj>(topic, BSON("topic" << topic)) );
	// Start thread to read from blackboard	
	if(subscriptions.size() == 1) {
		readMessageThread = new boost::thread(run, this);	
	}	
}

/**
 * Unsubscribe from a blackboard topic
 *
 * Unsubscribe from a blackboard topic, 
 * when there are no subscriptions to topics anymore, the thread
 * that reads the messages from the blackboard will be interrupted
 *
 * @param topic the name of the topic 
 **/
void BlackboardCppClient::unsubscribe(const std::string &topic) { 
	subscriptions.erase(topic);
	if(subscriptions.size() == 0) {
		readMessageThread->interrupt();	
	}
}

/**
 * Set the callback function
 **/
void BlackboardCppClient::setCallback(BlackboardSubscriber *func) {
	callback = func;
}

/**
 * Function is executed by all the threads to find out if there 
 * happened something for the topics subscribed to.
 * @param client The instance of the BlackboardCppClient
 **/
void BlackboardCppClient::run(BlackboardCppClient* client) {
	// Create namespace string
	std::string name = client->database;
	name.append(".");
	name.append(client->collection);
	mongo::Query where = QUERY("ns" << name);
	std::string id = "";
	std::string operation = "";

	while(true) {
		std::auto_ptr<mongo::DBClientCursor> tailedCursor = client->connection.query("local.oplog.rs", where, 0, 0, 0, 
			mongo::QueryOption_CursorTailable | mongo::QueryOption_AwaitData );
		// Iterate over all messages already in the collection, so that only new messages will be processed
		std::cout << "Number of messages skipped " << tailedCursor->itcount() << std::endl;
		while(true) {
			if(!tailedCursor->more()) {
				if(tailedCursor->isDead()) {
					break;
				}
				continue;
			}
			mongo::BSONObj addedObject = tailedCursor->next();
			mongo::BSONElement out;
			if(addedObject.hasField("o")) {
				addedObject.getObjectField("o").getObjectID(out);
				id = out.OID().toString();
				std::cout << id << std::endl;
			}
			if(addedObject.hasField("o2") && id.empty()) {
				addedObject.getObjectField("o2").getObjectID(out);
				id = out.OID().toString();
				std::cout << id << std::endl;
			}
			operation = addedObject.getStringField("op");
			std::vector<mongo::BSONObj> values;
		    for(std::map<std::string, mongo::BSONObj>::iterator it = client->subscriptions.begin(); it != client->subscriptions.end(); it++) {
        		values.push_back(it->second);
    		}
			mongo::BSONObj messageCheckObject(BSON("_id" << mongo::OID(id) << "$or" << values)); 
			mongo::BSONObj message = client->connection.findOne(name, messageCheckObject);
			std::cout << "message" << message.toString() << std::endl;
			/* If the message is not empty, it means something has changed on the topics
				subscribed to */
			if(!message.isEmpty()) {
				BlackboardSubscriber::BlackboardEvent event = BlackboardSubscriber::UNKNOWN;
				if(operation.compare("i") == 0) {
					event = BlackboardSubscriber::ADD;
				} else if(operation.compare("u") == 0) {
					event = BlackboardSubscriber::UPDATE;
				} else if(operation.compare("r") == 0) {
					event = BlackboardSubscriber::REMOVE;
				}
				client->callback->blackboardReadCallback(event, message.jsonString());
			}
		}
	}
}

