/**
 * @file StateMachine.h
 * @brief Statemachine Implementation
 * @date Created: 2013-17-04
 *
 * @author Gerben Boot & Joris Vergeer
 *
 * @section LICENSE
 * License: newBSD
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

#include "rexos_statemachine/StateMachine.h"
#include <boost/bind.hpp>

#include <cstdlib>

using namespace rexos_statemachine;

/**
 * Create a stateMachine
 * @param moduleID the unique identifier for the module that implements the statemachine
 **/
StateMachine::StateMachine(std::string nodeName,std::vector<rexos_statemachine::Mode> modes):
		listener(NULL),
		currentState(STATE_SAFE),
		currentMode(MODE_NORMAL),
		modes(modes),
		changeStateActionServer(nodeHandle, nodeName + "/change_state", boost::bind(&StateMachine::onChangeStateAction, this, _1), false),
		changeModeActionServer(nodeHandle, nodeName + "/change_mode", boost::bind(&StateMachine::onChangeModeAction, this, _1), false),
		transitionSetupServer(nodeHandle, nodeName + "/transition_setup", boost::bind(&StateMachine::onTransitionSetupAction, this, &transitionSetupServer), false),
		transitionShutdownServer(nodeHandle, nodeName + "/transition_shutdown", boost::bind(&StateMachine::onTransitionShutdownAction, this, &transitionShutdownServer), false),
		transitionStartServer(nodeHandle, nodeName + "/transition_start", boost::bind(&StateMachine::onTransitionStartAction, this, &transitionStartServer), false),
		transitionStopServer(nodeHandle, nodeName + "/transition_stop", boost::bind(&StateMachine::onTransitionStopAction, this, &transitionStopServer), false),
		changeStateActionClient(nodeHandle, nodeName + "/change_state"),
		changeModeActionClient(nodeHandle, nodeName + "/change_mode")
{

	StatePair transitionSetupStatePair(STATE_SAFE, STATE_STANDBY);
	StatePair transitionStartStatePair(STATE_STANDBY, STATE_NORMAL);
	StatePair transitionStopStatePair(STATE_NORMAL, STATE_STANDBY);
	StatePair transitionShutdownStatePair(STATE_STANDBY, STATE_SAFE);

	TransitionActionClient* transitionActionClientShutdown = new TransitionActionClient(nodeName + "/transition_shutdown",true);
	TransitionActionClient* transitionActionClientSetup = new TransitionActionClient(nodeName + "/transition_setup",true);
	TransitionActionClient* transitionActionClientStop = new TransitionActionClient(nodeName + "/transition_stop",true);
	TransitionActionClient* transitionActionClientStart = new TransitionActionClient(nodeName + "/transition_start",true);

	Transition *transitionShutdown = new Transition{transitionActionClientShutdown, STATE_SHUTDOWN};
	Transition *transitionSetup = new Transition{transitionActionClientSetup, STATE_SETUP};
	Transition *transitionStop = new Transition{transitionActionClientStop, STATE_STOP};
	Transition *transitionStart = new Transition{transitionActionClientStart, STATE_START};

	transitionMap[transitionSetupStatePair]= {transitionSetup,transitionShutdown,transitionSetupStatePair};
	transitionMap[transitionShutdownStatePair]= {transitionShutdown,NULL,transitionShutdownStatePair};
	transitionMap[transitionStartStatePair]= {transitionStart,transitionStop,transitionStartStatePair};
	transitionMap[transitionStopStatePair]= {transitionStop,NULL,transitionStopStatePair};

	modePossibleStates[MODE_NORMAL] = {STATE_NORMAL,STATE_STANDBY,STATE_SAFE, STATE_SETUP, STATE_SHUTDOWN, STATE_START, STATE_STOP};
	modePossibleStates[MODE_SERVICE] = {STATE_NORMAL,STATE_STANDBY,STATE_SAFE, STATE_SETUP, STATE_SHUTDOWN, STATE_START, STATE_STOP};
	modePossibleStates[MODE_ERROR] = {STATE_STANDBY,STATE_SAFE,STATE_SETUP,STATE_SHUTDOWN,STATE_STOP};
	modePossibleStates[MODE_CRITICAL_ERROR] = {STATE_SAFE,STATE_STOP,STATE_SHUTDOWN};
	modePossibleStates[MODE_E_STOP] = {STATE_SAFE};
	modePossibleStates[MODE_LOCK] = {STATE_NORMAL,STATE_STANDBY,STATE_SAFE, STATE_STOP};
	modePossibleStates[MODE_STEP] = {STATE_NORMAL,STATE_STANDBY,STATE_SAFE, STATE_SETUP, STATE_SHUTDOWN, STATE_START, STATE_STOP};

	changeStateActionServer.start();
	changeModeActionServer.start();

	transitionSetupServer.start();
	transitionShutdownServer.start();
	transitionStartServer.start();
	transitionStopServer.start();
}

StateMachine::~StateMachine() {
	delete listener;
}

void StateMachine::onTransitionSetupAction(TransitionActionServer* as){
	transitionSetup(as);
}

void StateMachine::onTransitionShutdownAction(TransitionActionServer* as){
	transitionShutdown(as);
}

void StateMachine::onTransitionStartAction(TransitionActionServer* as){
	transitionStart(as);
}

void StateMachine::onTransitionStopAction(TransitionActionServer* as){
	transitionStop(as);
}

State StateMachine::getCurrentState() {
	return currentState;
}

Mode StateMachine::getCurrentMode() {
	return currentMode;
}

/**
 * method to change te state from a subclass of the statemachine.
 * the method send a _changeState action to itself so the action call hasn't implement in the subclass 
 * @param desiredState is the desiredState
 * @param changeStateActionClient = NULL when not given. By this param it is possilbe to waitForResult and getState when finished(SUCCEEDED/ABORTED)
 **/
void StateMachine::changeState(State desiredState) {
	ROS_INFO("changeState called with desiredState %s",rexos_statemachine::state_txt[desiredState]);
	ChangeStateGoal goal;
	goal.desiredState = desiredState;
	changeStateActionClient.sendGoal(goal);
}

/**
 * method to change te mode from a subclass of the statemachine.
 * the method send a _changeMode action to itself so the action call hasn't implement in the subclass 
 * @param desiredMode is the desiredMode
 * @param changeModeActionClient = NULL when not given. By this param it is possilbe to waitForResult and getState when finished(SUCCEEDED/ABORTED)
 **/
void StateMachine::changeMode(Mode desiredMode) {
	ROS_INFO("_changeMode called with desiredMode %s",rexos_statemachine::state_txt[desiredMode]);
	ChangeModeGoal goal;
	goal.desiredMode = desiredMode;
	changeModeActionClient.sendGoal(goal);
}

void StateMachine::onChangeStateAction(const ChangeStateGoalConstPtr& goal){
	if(_changeState((rexos_statemachine::State) goal->desiredState))
		changeStateActionServer.setSucceeded();
	else
		changeStateActionServer.setAborted();
}

void StateMachine::onChangeModeAction(const ChangeModeGoalConstPtr& goal){
	if(_changeMode( (rexos_statemachine::Mode) goal->desiredMode))
		changeModeActionServer.setSucceeded();
	else
		changeModeActionServer.setAborted();
}

/**
 * Callback for the requestStateChange topic
 * Will lookup the transition function and execute it
 * @param request Contains the params for the state change
 * @param response Will tell if the state transition was succesfull for the state change
 **/
bool StateMachine::_changeState(rexos_statemachine::State newState) {
	TransitionMap::iterator it = transitionMap.find(StatePair(currentState, newState));
	if (it == transitionMap.end()) {
		return false;
	}

	ChangeStateEntry changeStateEntry = it->second;

	if (!statePossibleInMode(changeStateEntry.transition->transitionState, currentMode) )
		return false;

	_setState(changeStateEntry.transition->transitionState);		//set the currentState on the transitionState
	TransitionActionClient* transitionActionClient = changeStateEntry.transition->transitionActionClient;
	TransitionGoal goal;
	transitionActionClient->sendGoal(goal);
	transitionActionClient->waitForResult();
	while( rexos_statemachine::is_transition_state[currentState] ){
		if(currentState == changeStateEntry.transition->transitionState){
			if (transitionActionClient->getState() == actionlib::SimpleClientGoalState::SUCCEEDED){
				_setState(changeStateEntry.statePair.second);
			} else if(changeStateEntry.abortTransition == NULL) {
				throw new std::logic_error("Trying to access a null abortTransitions state");
			} else if(transitionActionClient->getState() == actionlib::SimpleClientGoalState::PREEMPTED) {
			} else if(transitionActionClient->getState() == actionlib::SimpleClientGoalState::PENDING) {
			} else{
				_setState(changeStateEntry.abortTransition->transitionState);
				transitionActionClient = changeStateEntry.abortTransition->transitionActionClient;
			}
		}else if (transitionActionClient->getState() == actionlib::SimpleClientGoalState::SUCCEEDED){
			_setState(changeStateEntry.statePair.first);
			TransitionGoal goal;
			transitionActionClient->sendGoal(goal);
			transitionActionClient->waitForResult();
		}else{
			//ABORT TRANSITION FAILED
			_setState(changeStateEntry.statePair.first);
			TransitionGoal goal;
			transitionActionClient->sendGoal(goal);
			transitionActionClient->waitForResult();
		}
	}

	_forceToAllowedState();

	return currentState == it->first.second;
}

bool StateMachine::_changeMode(Mode newMode) {
	bool succeeded = false;
	for(Mode mode : modes){
		if(mode == newMode)
			succeeded = true;
	}

	if(!succeeded)
		return false;

	_setMode(newMode);
	if(newMode == MODE_E_STOP)
		_setState(STATE_SAFE);
	
	_forceToAllowedState();
	return true;
}

bool StateMachine::statePossibleInMode(State state, Mode mode) {
	std::vector<State> States = modePossibleStates[mode];
	for (int i = 0; i < States.size(); i++) {
		if (States[i] == state)
			return true;
	}
	return false;
}

void StateMachine::_forceToAllowedState() {
	while (!statePossibleInMode(currentState, currentMode)) {
		switch (currentState) {
		case STATE_NORMAL:
			_changeState(STATE_STANDBY);
			break;
		case STATE_STANDBY:
			_changeState(STATE_SAFE);
			break;
		default:
			break;
		}
	}
}

void StateMachine::setListener(Listener* listener) {
	this->listener = listener;
}

void StateMachine::_setState(State state) {
	currentState = state;
	if (listener != NULL) {
		listener->onStateChanged();
	}
}

void StateMachine::_setMode(Mode mode) {
	currentMode = mode;
	if (listener != NULL) {
		listener->onModeChanged();
	}
}
