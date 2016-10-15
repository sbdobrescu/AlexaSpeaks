/**
 *  Alexa Speaks
 *
 *
 *  10/14/2016  version 0.0.1c		Added Sonos support and OAuth tokens to logs for copy and paste
 *	10/11/2016	version 0.0.1b		Fixed audio output for both media and synth
 *	10/10/2016 	Version 0.0.1a		Added media player support
 *	10/09/2016	Version 0.0.1		Initial File
 * This is a test
 *
 *  Copyright 2016 Jason Headley
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Alexa Speaks",
    namespace: "bamarayne",
    author: "Jason Headley",
    description: "A free-form Speech-to-Text generator using the Amazon Echo (Alexa) device.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")
preferences {
    page name:"mainPage"
    page name:"pageReset"
    page name:"pageAbout"
    page name:"pageConfiguration"
}
//Show main page
def mainPage() {
    dynamicPage(name: "mainPage", title:"Alexa Speaks", install: true, uninstall: false) {
        section("Options") {
			href "pageConfiguration", title: "Configuration", description: none, 
            	image: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png"
  				image: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png"
			href "pageAbout", title: "About ${textAppName()}", description: "Tap to get application version, license, instructions or remove the application",
            	image: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png"
        }
	  }
}
def pageAbout(){
	dynamicPage(name: "pageAbout", uninstall: true) {
        section {
        	paragraph "${textAppName()}\n${textVersion()}\n${textCopyright()}",image: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png"
        }
        section ("Access Token / Application ID"){
            if (!state.accessToken) {
				OAuthToken()
			}
            def msg = state.accessToken != null ? state.accessToken : "Could not create Access Token. OAuth may not be enabled. Go to the SmartApp IDE settings to enable OAuth."
            paragraph "\nAccess Token:\n${msg}\n\nApplication ID:\n${app.id}"
    	}
        section ("Apache License"){
        	paragraph textLicense()
        }
        section("Tap button below to remove the application"){
        }
	  }
}
def pageConfiguration(){
    dynamicPage(name: "pageConfiguration", title: "Configuration", uninstall: true){
		section("Media Player Devices (Sonos, wi-fi, etc...)"){
       		input "mediaDevice", "capability.musicPlayer", title: "Choose Speaker(s)", multiple: true, required: false, submitOnChange: true
       	 	input "volume", "number", title: "Speaker Volume", description: "0-100%", required: false
    	}
        section("Speech Synthesizer Devices (LanDroid, etc...)"){
        	input "synthDevice", "capability.speechSynthesis", title: "Choose Speaker(s)", multiple: true, required: false, submitOnChange: true
    	}
			section("Security Settings"){
            href "pageReset", title: "Reset Access Token", description: "Tap to revoke access token."
        }
        section("Rename App"){
        	label title:"Rename App (Optional)", required:false, defaultValue: "Alexa Speaks"
    	 	}
        }
    }

def pageReset(){
	dynamicPage(name: "pageReset", title: "Access Token Reset"){
        section{
			state.accessToken = null
            OAuthToken()
            def msg = state.accessToken != null ? "New access token:\n${state.accessToken}\n\nClick 'Done' above to return to the previous menu." : "Could not reset Access Token. OAuth may not be enabled. Go to the SmartApp IDE settings to enable OAuth."
	    	paragraph "${msg}"
		    }
	  }
}
def installed() {
log.debug "Installed with settings: ${settings}"
log.trace "STappID = '${app.id}' , STtoken = '${state.accessToken}'"
initialize()
}
def updated() {
log.debug "Updated with settings: ${settings}"
log.trace "STappID = '${app.id}' , STtoken = '${state.accessToken}'"
initialize()
}
def initialize() {
	if (!state.accessToken) {
		log.error "Access token not defined. Ensure OAuth is enabled in the SmartThings IDE."
	}
}
def getURLs(){
	def Name = params.Name, url = formatURL("${getApiServerUrl()}/api/smartapps/installations/${app.id}/&access_token=${state.accessToken}")
    def result = "<div style='padding:10px'>Copy the URL below and paste it to your control application.</div><div style='padding:10px'>Click '<' above to return to the Alexa Speaks SmartApp.</div>"
	result += "<div style='padding:10px;'><b>Macro REST URL:</b></div>"
	result += "<textarea rows='5' style='width: 99%'>${url}</textarea>"
	result += "<hr>"
    displayData(result)
}
def setupData(){
	log.info "Set up web page located at : ${getApiServerUrl()}/api/smartapps/installations/${app.id}/setup?access_token=${state.accessToken}"
    def result ="<div style='padding:10px'><i><b><a href='http://aws.amazon.com' target='_blank'>Lambda</a> code variables:</b></i><br><br>var STappID = '${app.id}';<br>var STtoken = '${state.accessToken}';<br>"
    result += "var url='${getApiServerUrl()}/api/smartapps/installations/' + STappID + '/' ;<br><br><hr><br><br>"}
mappings {
      path("/r") {action: [GET: "readData"]}
      path("/w") {action: [GET: "writeData"]}
      path("/t") {action: [GET: "processTts"]}
      path("/u") { action: [GET: "getURLs"] }
      path("/setup") { action: [GET: "setupData"] }
}
def writeData() {
    log.debug "Command received with params $params"
	def command = params.c  	//The action you want to take i.e. on/off 
	def label = params.l		//The name given to the device by you
	}
def readData() {
    log.debug "Command received with params $params"
	def label = params.l		//The name given to the device by you
}
def processTts() {
	def tts = params.ttstext
	tts = "Excuse me: "+ tts
		if (synthDevice) synthDevice.speak(tts)
		if (tts) {
			state.sound = textToSpeech(tts instanceof List ? tts[0] : tts) // not sure why this is (sometimes) needed)
		}
		else {
			state.sound = textToSpeech("You selected the custom message option but did not enter a message in the $app.label Smart App")
		}
		if (mediaDevice) {
			mediaDevice.playTrackAndResume(state.sound.uri, state.sound.duration, volume)
			log.trace "${state.sound}"
	}
}
//Common Code
def OAuthToken(){
	try {
		createAccessToken()
		log.debug "Creating new Access Token"
	} catch (e) {
		log.error "Access Token not defined. OAuth may not be enabled. Go to the SmartApp IDE settings to enable OAuth."
	}
}
//Version/Copyright/Information/Help
private def textAppName() {
	def text = "Alexa Speaks"
}	
private def textVersion() {
    def text = "Version 0.0.1c (10/14/2016)"
}
private def textCopyright() {
    def text = "Copyright © 2016 Jason Headley"
}
private def textLicense() {
	def text =
		"Licensed under the Apache License, Version 2.0 (the 'License'); "+
		"you may not use this file except in compliance with the License. "+
		"You may obtain a copy of the License at"+
		"\n\n"+
		"    http://www.apache.org/licenses/LICENSE-2.0"+
		"\n\n"+
		"Unless required by applicable law or agreed to in writing, software "+
		"distributed under the License is distributed on an 'AS IS' BASIS, "+
		"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. "+
		"See the License for the specific language governing permissions and "+
		"limitations under the License."
}
private def textHelp() {
	def text =
		"This app allows you to speak freely to your Alexa device and have it repeated back on a remote playback device"
}
