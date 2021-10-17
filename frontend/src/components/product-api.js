import axios from 'axios'

const AXIOS = axios.create({
    baseURL: `/product`,
    timeout: 5000
});

export default {

    refreshGoodSKU(text, flag, attribute, cancelToken, count, skip) {
        var filter = {text: text, flag: flag, attribute: attribute, count: count, skip: skip}
        var json = [];
        json.push(filter);
        return AXIOS.post(`/refreshGoodSku/`, json, {cancelToken: cancelToken.token});
    },
    refreshRawSKU(text, flag, isWithoutConfirm, employee, cancelToken, count, skip) {
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm, attribute: employee, count: count, skip: skip}
        var json = [];
        json.push(filter);
        return AXIOS.post(`/refreshRawSku/`, json, {cancelToken: cancelToken.token});
    },
    getGoodSku(id){
        return AXIOS.get('/getSku/' + id)
    },

    getGroupList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getGroupList/', json)
    },
    getClientGroupList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getClientGroupList/', json)
    },
    getSubGroupList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getSubGroupList/', json)
    },
    getTypeList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getTypeList/', json)
    },
    getGoodsList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getGoodsList/', json)
    },
    getBrandList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getBrandList/', json)
    },
    getSubBrandList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getSubBrandList/', json)
    },
    getColorList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getColorList/', json)
    },
    getFlavorList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getFlavorList/', json)
    },
    getExtraList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getExtraList/', json)
    },
    getPackageList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getPackageList/', json)
    },
    getQuantityList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getQuantityList/', json)
    },
    getManufacturerList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getManufacturerList/', json)
    },
    getOriginList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getOriginList/', json)
    },
    getABVList(text, flag, isWithoutConfirm){
        var filter = {text: text, flag: flag, isWithoutConfirm: isWithoutConfirm}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getABVList/', json)
    },
    getClassList(){
        return AXIOS.get('/getClassList')
    },
    createSku(sku){
        return AXIOS.post('/createSku', sku)
    },
    updateSku(sku){
        return AXIOS.post('/updateSku', sku)
    },
    confirmSku(rawId, goodId, goodSku){
        var confirmedSku = {rawId: rawId, goodId: goodId, goodSku: goodSku}
        var json = [];
        json.push(confirmedSku);
        return AXIOS.post('/confirmSku', json);
    },
    addProperty(table, value, flag){
        var newProperty = {table: table, value: value, flag: flag}
        var json = [];
        json.push(newProperty);
        return AXIOS.post('/addProperty', json)
    },
    changeProperty(table, value, flag, id){
        var newProperty = {table: table, value: value, flag: flag, id: id}
        var json = [];
        json.push(newProperty);
        return AXIOS.post('/changeProperty', json)
    },
    loadSKU(jsonData){
        return AXIOS.post('/loadsku', jsonData)
    },
    getCounters(text, flag, assignee, isWithoutConfirm, cancelToken){
        var filter = {text: text.toLowerCase(), flag: flag, isWithoutConfirm: isWithoutConfirm, attribute: assignee.toLowerCase()}
        var json = [];
        json.push(filter);
        return AXIOS.post('/getCounters', json, {cancelToken: cancelToken.token})
    },
    getLastGoodProduct(){
        return AXIOS.get('/reconnectedGoodProduct')
    },
    getLastConfirmedProduct(){
        return AXIOS.get('/reconnectedRawProduct')
    },
    getActionLog(){
        return AXIOS.get('/getActionLog')
    }
}


