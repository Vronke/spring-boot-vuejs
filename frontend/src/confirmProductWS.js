import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'


var stompClient = null;
const handlers = []
var isFirstConnectedConfirmGoodProduct = false;

export function isConnectedConfirmGoodProduct() {
    if (isFirstConnectedConfirmGoodProduct)
        return stompClient.connected;
}

export function connectConfirmProduct() {
    const socket = new SockJS('/gs-guide-websocket')
    stompClient = Stomp.over(socket)
    stompClient.disconnect();
    stompClient.connect({}, frame => {
        console.log('Connected: ' + frame)
        stompClient.subscribe('/topic/confirmProduct', message => {
            handlers.forEach(handler => handler(JSON.parse(message.body)))
        })
        isFirstConnectedConfirmGoodProduct = true;
    })
    stompClient.reconnect_delay = 20000;
}

export function addHandlerConfirmProduct(handler) {
    handlers.push(handler)
}

export function disconnectConfirmGoodProduct() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    isFirstConnectedConfirmGoodProduct = false;
    console.log("Disconnected");
}

export function sendConfirmProduct(product) {
    stompClient.send("/app/confirmSku", {}, JSON.stringify(product))
}



