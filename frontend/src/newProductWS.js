import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'


var stompClient = null;
const handlers = []
var isFirstConnectedGoodProduct = false;

export function isConnectedGoodProduct() {
    if (isFirstConnectedGoodProduct)
        return stompClient.connected;
}

export function getStomp() {
    console.log(stompClient);
}

export function connectGoodProduct() {
    const socket = new SockJS('/gs-guide-websocket')
    stompClient = Stomp.over(socket)
    stompClient.disconnect();
    stompClient.connect({}, frame => {
        console.log('Connected: ' + frame)
        stompClient.subscribe('/topic/createProduct', message => {
            handlers.forEach(handler => handler(JSON.parse(message.body)))
        })
        isFirstConnectedGoodProduct = true;
    })
    console.log(stompClient)
}

export function addHandlerGoodProduct(handler) {
    handlers.push(handler)
}

export function disconnectGoodProduct() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    isFirstConnectedGoodProduct = false;
    console.log("Disconnected");
}

export function sendGoodProduct(product) {
    stompClient.send("/app/createSku", {}, JSON.stringify(product))
}



