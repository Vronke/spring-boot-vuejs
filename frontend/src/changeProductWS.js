import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'


var stompClient = null;
const handlers = []
var isFirstConnectedChangeGoodProduct = false;

export function isConnectedChangeGoodProduct() {
    if (isFirstConnectedChangeGoodProduct)
        return stompClient.connected;
}

export function connectChangeGoodProduct() {
    const socket = new SockJS('/gs-guide-websocket')
    stompClient = Stomp.over(socket)
    stompClient.disconnect();
    stompClient.connect({}, frame => {
        console.log('Connected: ' + frame)
        stompClient.subscribe('/topic/changeProduct', message => {
            handlers.forEach(handler => handler(JSON.parse(message.body)))
        })
        isFirstConnectedChangeGoodProduct = true;
    })
}

export function addHandlerChangeGoodProduct(handler) {
    handlers.push(handler)
}

export function disconnectChangeGoodProduct() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    isFirstConnectedChangeGoodProduct = false;
    console.log("Disconnected");
}

export function sendChangeGoodProduct(product) {
    stompClient.send("/app/updateSku", {}, JSON.stringify(product))
}



