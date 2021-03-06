 /**
 * @file InstructionData.cpp
 * @brief Represents the instructiondata of an equipletstep.
 * @date Created: 2012-09-19
 *
 * @author Alexander Streng
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

#include "rexos_datatypes/InstructionData.h"

namespace rexos_datatypes{

    InstructionData::InstructionData() {
    }

    std::string InstructionData::getCommand(){
        return this->command;
    }
    void InstructionData::setCommand(std::string command){
        this->command = command;
    }

    std::string InstructionData::getDestination(){
        return this->destination;
    }
    void InstructionData::setDestination(std::string destination){
        this->destination = destination;
    }

    std::string InstructionData::getLook_up(){
        return this->look_up;
    }
    void InstructionData::setLook_up(std::string look_up){
        this->look_up = look_up;
    }

    std::map<std::string, std::string> InstructionData::getLook_up_parameters(){
        return this->look_up_parameters;
    }
    void InstructionData::setLook_up_parameters(map<std::string, std::string> look_up_parameters){
        this->look_up_parameters = look_up_parameters;
    }

    std::map<std::string, std::string> InstructionData::getPayload(){
        return this->payload;    
    }
    void InstructionData::setPayload(std::map<std::string, std::string> payload){
        this->payload = payload;
    }

    JSONNode InstructionData::getJsonNode(){
        return this-> jsonNode;
    }

    void InstructionData::setJsonNode(JSONNode jsonNode){
        this->jsonNode = jsonNode;
    }

}