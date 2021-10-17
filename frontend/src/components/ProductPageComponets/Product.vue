<template>
  <div v-on:click="click($event.target)">
    <div id="filters">
      <div id="rawFilter">
        <input class="filter" placeholder="Фильтр" type="text" v-model="rawFilterInput" v-on:input="refreshRaw(rawFilterInput)"
               v-on:keydown.enter="refreshRaw(rawFilterInput)">
        <input class="filter" placeholder="Сотрудник" type="text" v-model="employeeFilterInput" v-on:input="refreshRaw(rawFilterInput)"
               v-on:keydown.enter="refreshRaw(rawFilterInput)" style="max-width: 35%">
      </div>
      <div id="textBlock" style="align-self: start">
        <div id="confirmSkuCheckbox">
          <input type="checkbox" v-model="isWithoutConfirm" v-on:change="refreshRaw(rawFilterInput)" style="margin-top: auto; margin-bottom: auto">
          <p style="margin: 0px; padding-top: 5px; height: 30px; min-width: 150px; text-align: center">Только без соответствия</p>
        </div>
        <p id="filteredRawCounter" style="margin: 2px">Найдено сырых: {{filteredRawCounter}}</p>
      </div>
      <div id="goodFilter">
        <input ref="filter" class="filter" placeholder="Фильтр" type="text" v-model="goodFilterInput" v-on:input="refreshGood(goodFilterInput)"
               v-on:keydown.enter="refreshGood(goodFilterInput)">
        <input class="filter" placeholder="Поиск по атрибуту" type="text" v-model="attributeFilterInput" v-on:input="refreshGood(goodFilterInput)"
               v-on:keydown.enter="refreshGood(goodFilterInput)" style="max-width: 35%">
      </div>
      <div style="width: 10%">
        <select id="productGroupSelect" ref="selectedRawGroup" v-on:change="changeProductClass()"  tabindex="-1">
          <option v-for="group in productRawGroupList">{{group.text}}</option>
        </select>
      </div>
    </div>
    <div id="page">
      <div id="pageRawSku" v-infinite-scroll="loadMoreRawProducts" infinite-scroll-disabled="busyRaw" infinite-scroll-distance="10">
        <table class="table" id="rawSkuTable">
          <thead>
          <tr>
            <th ref="thId">
              <div class="columnName">
                <div v-on:click="sortRawBy('id')" style="width: 100%">id</div>
                <div class="resizable" ref="resId"></div>
              </div>
            </th>
            <th ref="thRawSku">
              <div class="columnName">
                <div v-on:click="sortRawBy('sku')" style="width: 100%">rawSku</div>
                <div class="resizable" ref="resRawSku"></div>
              </div>
            </th>
            <th v-on:click="sortRawBy('goodSku')">goodSku</th>
          </tr>
          </thead>
          <tbody id="rawTableBody">
          <tr v-for="sku in rawSkuList"  v-on:click="selectRawRow(sku); isFocusOnRawSkuList = true; isFocusOnGoodSkuList = false"
              :id="'id' + sku.id"
              :class="{'activeGood' : sku.hover}">
            <td>{{sku.id}}</td>
            <td>{{sku.sku}}</td>
            <td>{{sku.goodSku}}</td>
          </tr>
          </tbody>
        </table>
      </div>
      <div id="pageGoodSku">
        <div id="tableGoodSku" v-infinite-scroll="loadMoreGoodProducts" infinite-scroll-disabled="busyGood" infinite-scroll-distance="10">
          <table class="table" id="goodSkuTable">
            <thead>
            <tr>
              <th ref="thGoodId">
                <div class="columnName">
                  <div v-on:click="sortGoodBy('id')" style="width: 100%">id</div>
                  <div class="resizable" ref="resGoodId"></div>
                </div>
              </th>
              <th v-on:click="sortGoodBy('sku')">sku</th>
            </tr>
            </thead>
            <tbody id="goodTableBody">
            <tr v-for="sku in goodSkuList" v-on:click="selectGoodRow(sku); isFocusOnGoodSkuList = true; isFocusOnRawSkuList = false"
                :id="'id' + sku.id"
                :class="{'activeGood' : sku.hover}">
              <td>{{sku.id}}</td>
              <td>{{sku.sku}}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div style="height: 30px; display: flex; flex-wrap: nowrap">
          <p style="width: 30%; font-size: 12px; padding-top: 7px; text-align: start">Неразбитых СКЮ: {{rawCounter}}</p>
          <button class="buttonConfirm" v-on:click="confirmSku()" :disabled="isReconnectedConfirmGoodProductWS || isConfirmingSku">Дать соответствие</button>
        </div>
        <div class="skuPropLabel">
          <p class="label">Группа: </p>
          <input  v-on:keydown.down="downSelect(skuGroupList)" v-on:keydown.up="upSelect(skuGroupList)" v-on:keydown.35="endSelect(skuGroupList)" v-on:keydown.36="homeSelect(skuGroupList)" tabindex="1" v-on:keydown.tab="isFocusedGroup=false"
                  v-on:keydown.enter="enterSelect(skuGroupList, 'skuGroupList')" type="text" class="toolSearch" v-on:input="refreshSkuGroupList(false)" @focus="openSearchGroup()" @blur="closeSearchGroup()"
                  v-on:mouseenter="isMouseOnGroup = true" v-on:mouseleave="isMouseOnGroup = false" ref="groupList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedGroup" @blur="closeSearchGroup()"
                  v-on:mouseenter="isMouseOnGroup = true" v-on:mouseleave="isMouseOnGroup = false">
            <option v-for="group in skuGroupList" :id="'id' + group.id" class="groupOptions" v-on:click="selectGroup(group)" :class="{'hoverOption' : group.hover}"
                    v-on:mouseenter="hoverSelect(skuGroupList, group)">{{group.text}}</option>
          </select>
          <input class="inputProp" ref="groupInput" tabindex="2">
          <v-btn x-small icon color="black" class="buttonsProp" tile elevation=3
                 v-on:click="changeProperty('ProductGroup', $refs.groupInput.value, skuGroupList, $refs.groupList, selectedGroup)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp" tile elevation="3"
                 v-on:click="addProperty('ProductGroup', $refs.groupInput.value, skuGroupList, $refs.groupList, selectedGroup)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Группа_К: </p>
          <input v-on:keydown.down="downSelect(skuClientGroupList)" v-on:keydown.up="upSelect(skuClientGroupList)" v-on:keydown.35="endSelect(skuClientGroupList)" v-on:keydown.36="homeSelect(skuClientGroupList)" tabindex="3" v-on:keydown.tab="isFocusedClientGroup=false"
                 v-on:keydown.enter="enterSelect(skuClientGroupList, 'skuClientGroupList')" type="text" class="toolSearch" v-on:input="refreshSkuClientGroupList(false)" @focus="openSearchClientGroup()" @blur="closeSearchClientGroup()"
                 v-on:mouseenter="isMouseOnClientGroup = true" v-on:mouseleave="isMouseOnClientGroup = false" ref="clientGroupList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedClientGroup" @blur="closeSearchClientGroup()"
                  v-on:mouseenter="isMouseOnClientGroup = true" v-on:mouseleave="isMouseOnClientGroup = false">
            <option v-for="clientGroup in skuClientGroupList" :id="'id' + clientGroup.id" class="groupOptions" v-on:click="selectClientGroup(clientGroup)" :class="{'hoverOption' : clientGroup.hover}"
                    v-on:mouseenter="hoverSelect(skuClientGroupList, clientGroup)">{{clientGroup.text}}
            </option>
          </select>
          <input class="inputProp" ref="clientGroupInput" tabindex="4">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductClientGroup', $refs.clientGroupInput.value, skuClientGroupList, $refs.clientGroupList, selectedClientGroup)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductClientGroup', $refs.clientGroupInput.value, skuClientGroupList, $refs.clientGroupList, selectedClientGroup)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Подгруппа: </p>
          <input v-on:keydown.down="downSelect(skuSubGroupList)" v-on:keydown.up="upSelect(skuSubGroupList)" v-on:keydown.35="endSelect(skuSubGroupList)" v-on:keydown.36="homeSelect(skuSubGroupList)" tabindex="5" v-on:keydown.tab="isFocusedSubGroup=false"
                 v-on:keydown.enter="enterSelect(skuSubGroupList, 'skuSubGroupList')" type="text" class="toolSearch" v-on:input="refreshSkuSubGroupList(false)" @focus="openSearchSubGroup()" @blur="closeSearchSubGroup()"
                 v-on:mouseenter="isMouseOnSubGroup = true" v-on:mouseleave="isMouseOnSubGroup = false" ref="subGroupList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedSubGroup" @blur="closeSearchSubGroup()"
                  v-on:mouseenter="isMouseOnSubGroup = true" v-on:mouseleave="isMouseOnSubGroup = false">
            <option v-for="subGroup in skuSubGroupList" :id="'id' + subGroup.id" class="groupOptions" v-on:click="selectSubGroup(subGroup)" :class="{'hoverOption' : subGroup.hover}"
                    v-on:mouseenter="hoverSelect(skuSubGroupList, subGroup)">{{subGroup.text}}</option>
          </select>
          <input class="inputProp" ref="subGroupInput" tabindex="6">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductSubgroup', $refs.subGroupInput.value, skuSubGroupList, $refs.subGroupList, selectedSubGroup)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductSubgroup', $refs.subGroupInput.value, skuSubGroupList, $refs.subGroupList, selectedSubGroup)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Тип: </p>
          <input v-on:keydown.down="downSelect(skuTypeList)" v-on:keydown.up="upSelect(skuTypeList)" v-on:keydown.35="endSelect(skuTypeList)" v-on:keydown.36="homeSelect(skuTypeList)" tabindex="7" v-on:keydown.tab="isFocusedType=false"
                 v-on:keydown.enter="enterSelect(skuTypeList, 'skuTypeList')" type="text" class="toolSearch" v-on:input="refreshSkuTypeList(false)" @focus="openSearchType()" @blur="closeSearchType()"
                 v-on:mouseenter="isMouseOnType = true" v-on:mouseleave="isMouseOnType = false" ref="typeList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedType" @blur="closeSearchType()"
                  v-on:mouseenter="isMouseOnType = true" v-on:mouseleave="isMouseOnType = false">
            <option v-for="type in skuTypeList" :id="'id' + type.id" class="groupOptions" v-on:click="selectType(type)" :class="{'hoverOption' : type.hover}"
                    v-on:mouseenter="hoverSelect(skuTypeList, type)">{{type.text}}</option>
          </select>
          <input class="inputProp" ref="typeInput" tabindex="8">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductType', $refs.typeInput.value, skuTypeList, $refs.typeList, selectedType)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductType', $refs.typeInput.value, skuTypeList, $refs.typeList, selectedType)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Товар: </p>
          <input v-on:keydown.down="downSelect(skuGoodsList)" v-on:keydown.up="upSelect(skuGoodsList)" v-on:keydown.35="endSelect(skuGoodsList)" v-on:keydown.36="homeSelect(skuGoodsList)" tabindex="9" v-on:keydown.tab="isFocusedGoods=false"
                 v-on:keydown.enter="enterSelect(skuGoodsList, 'skuGoodsList')" type="text" class="toolSearch" v-on:input="refreshSkuGoodsList(false)" @focus="openSearchGoods()" @blur="closeSearchGoods()"
                 v-on:mouseenter="isMouseOnGoods = true" v-on:mouseleave="isMouseOnGoods = false" ref="goodsList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedGoods" @blur="closeSearchGoods()"
                  v-on:mouseenter="isMouseOnGoods = true" v-on:mouseleave="isMouseOnGoods = false">
            <option v-for="goods in skuGoodsList" :id="'id' + goods.id" class="groupOptions" v-on:click="selectGoods(goods)" :class="{'hoverOption' : goods.hover}"
                    v-on:mouseenter="hoverSelect(skuGoodsList, goods)">{{goods.text}}</option>
          </select>
          <input class="inputProp" ref="goodsInput" tabindex="10">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductGood', $refs.goodsInput.value, skuGoodsList, $refs.goodsList, selectedGoods)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductGood', $refs.goodsInput.value, skuGoodsList, $refs.goodsList, selectedGoods)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Бренд: </p>
          <input v-on:keydown.down="downSelect(skuBrandList)" v-on:keydown.up="upSelect(skuBrandList)" v-on:keydown.35="endSelect(skuBrandList)" v-on:keydown.36="homeSelect(skuBrandList)" tabindex="11" v-on:keydown.tab="isFocusedBrand=false"
                 v-on:keydown.enter="enterSelect(skuBrandList, 'skuBrandList')" type="text" class="toolSearch" v-on:input="refreshSkuBrandList(false)" @focus="openSearchBrand()" @blur="closeSearchBrand()"
                 v-on:mouseenter="isMouseOnBrand = true" v-on:mouseleave="isMouseOnBrand = false" ref="brandList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedBrand" @blur="closeSearchBrand()"
                  v-on:mouseenter="isMouseOnBrand = true" v-on:mouseleave="isMouseOnBrand = false">
            <option v-for="brand in skuBrandList" :id="'id' + brand.id" class="groupOptions" v-on:click="selectBrand(brand)" :class="{'hoverOption' : brand.hover}"
                    v-on:mouseenter="hoverSelect(skuBrandList, brand)">{{brand.text}}</option>
          </select>
          <input class="inputProp" ref="brandInput" tabindex="12">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductBrand', $refs.brandInput.value, skuBrandList, $refs.brandList, selectedBrand)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductBrand', $refs.brandInput.value, skuBrandList, $refs.brandList, selectedBrand)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Подбренд: </p>
          <input v-on:keydown.down="downSelect(skuSubBrandList)" v-on:keydown.up="upSelect(skuSubBrandList)" tabindex="13" v-on:keydown.tab="isFocusedSubBrand=false"
                 v-on:keydown.35="endSelect(skuSubBrandList)" v-on:keydown.36="homeSelect(skuSubBrandList)"
                 v-on:keydown.enter="enterSelect(skuSubBrandList, 'skuSubBrandList')" type="text" class="toolSearch" v-on:input="refreshSkuSubBrandList(false)" @focus="openSearchSubBrand()" @blur="closeSearchSubBrand()"
                 v-on:mouseenter="isMouseOnSubBrand = true" v-on:mouseleave="isMouseOnSubBrand = false" ref="subBrandList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedSubBrand" @blur="closeSearchSubBrand()" ref="subBrandSelect"
                  v-on:mouseenter="isMouseOnSubBrand = true" v-on:mouseleave="isMouseOnSubBrand = false">
            <option v-for="subBrand in skuSubBrandList" class="groupOptions" :id="'id' + subBrand.id" v-on:click="selectSubBrand(subBrand)" :class="{'hoverOption' : subBrand.hover}"
                    v-on:mouseenter="hoverSelect(skuSubBrandList, subBrand)">{{subBrand.text}}</option>
          </select>
          <input class="inputProp" ref="subBrandInput" tabindex="14">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductSubbrand', $refs.subBrandInput.value, skuSubBrandList, $refs.subBrandList, selectedSubBrand)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductSubbrand', $refs.subBrandInput.value, skuSubBrandList, $refs.subBrandList, selectedSubBrand)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Цвет: </p>
          <input v-on:keydown.down="downSelect(skuColorList)" v-on:keydown.up="upSelect(skuColorList)" v-on:keydown.35="endSelect(skuColorList)" v-on:keydown.36="homeSelect(skuColorList)" tabindex="15" v-on:keydown.tab="isFocusedColor=false"
                 v-on:keydown.enter="enterSelect(skuColorList, 'skuColorList')" type="text" class="toolSearch" v-on:input="refreshSkuColorList(false)" @focus="openSearchColor()" @blur="closeSearchColor()"
                 v-on:mouseenter="isMouseOnColor = true" v-on:mouseleave="isMouseOnColor = false" ref="colorList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedColor" @blur="closeSearchColor()"
                  v-on:mouseenter="isMouseOnColor = true" v-on:mouseleave="isMouseOnColor = false">
            <option v-for="color in skuColorList" :id="'id' + color.id" class="groupOptions" v-on:click="selectColor(color)" :class="{'hoverOption' : color.hover}"
                    v-on:mouseenter="hoverSelect(skuColorList, color)">{{color.text}}</option>
          </select>
          <input class="inputProp" ref="colorInput" tabindex="16">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductColour', $refs.colorInput.value, skuColorList, $refs.colorList, selectedColor)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductColour', $refs.colorInput.value, skuColorList, $refs.colorList, selectedColor)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Вкус: </p>
          <input v-on:keydown.down="downSelect(skuFlavorList)" v-on:keydown.up="upSelect(skuFlavorList)" v-on:keydown.35="endSelect(skuFlavorList)" v-on:keydown.36="homeSelect(skuFlavorList)" tabindex="17" v-on:keydown.tab="isFocusedFlavor=false"
                 v-on:keydown.enter="enterSelect(skuFlavorList, 'skuFlavorList')" type="text" class="toolSearch" v-on:input="refreshSkuFlavorList(false)" @focus="openSearchFlavor()" @blur="closeSearchFlavor()"
                 v-on:mouseenter="isMouseOnFlavor = true" v-on:mouseleave="isMouseOnFlavor = false" ref="flavorList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedFlavor" @blur="closeSearchFlavor()"
                  v-on:mouseenter="isMouseOnFlavor = true" v-on:mouseleave="isMouseOnFlavor = false">
            <option v-for="flavor in skuFlavorList" :id="'id' + flavor.id" class="groupOptions" v-on:click="selectFlavor(flavor)" :class="{'hoverOption' : flavor.hover}"
                    v-on:mouseenter="hoverSelect(skuFlavorList, flavor)">{{flavor.text}}</option>
          </select>
          <input class="inputProp" ref="flavorInput" tabindex="18">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductFlavour', $refs.flavorInput.value, skuFlavorList, $refs.flavorList, selectedFlavor)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductFlavour', $refs.flavorInput.value, skuFlavorList, $refs.flavorList, selectedFlavor)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Доп: </p>
          <input v-on:keydown.down="downSelect(skuExtraList)" v-on:keydown.up="upSelect(skuExtraList)" v-on:keydown.35="endSelect(skuExtraList)" v-on:keydown.36="homeSelect(skuExtraList)" tabindex="19" v-on:keydown.tab="isFocusedExtra=false"
                 v-on:keydown.enter="enterSelect(skuExtraList, 'skuExtraList')" type="text" class="toolSearch" v-on:input="refreshSkuExtraList(false)" @focus="openSearchExtra()" @blur="closeSearchExtra()"
                 v-on:mouseenter="isMouseOnExtra = true" v-on:mouseleave="isMouseOnExtra = false" ref="extraList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedExtra" @blur="closeSearchExtra()"
                  v-on:mouseenter="isMouseOnExtra = true" v-on:mouseleave="isMouseOnExtra = false">
            <option v-for="extra in skuExtraList" :id="'id' + extra.id" class="groupOptions" v-on:click="selectExtra(extra)" :class="{'hoverOption' : extra.hover}"
                    v-on:mouseenter="hoverSelect(skuExtraList, extra)">{{extra.text}}</option>
          </select>
          <input class="inputProp" ref="extraInput" tabindex="20">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductExtra', $refs.extraInput.value, skuExtraList, $refs.extraList, selectedExtra)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductExtra', $refs.extraInput.value, skuExtraList, $refs.extraList, selectedExtra)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Упаковка: </p>
          <input v-on:keydown.down="downSelect(skuPackList)" v-on:keydown.up="upSelect(skuPackList)" v-on:keydown.35="endSelect(skuPackList)" v-on:keydown.36="homeSelect(skuPackList)" tabindex="21" v-on:keydown.tab="isFocusedPack=false"
                 v-on:keydown.enter="enterSelect(skuPackList, 'skuPackList')" type="text" class="toolSearch" v-on:input="refreshSkuPackList(false)" @focus="openSearchPack()" @blur="closeSearchPack()"
                 v-on:mouseenter="isMouseOnPack = true" v-on:mouseleave="isMouseOnPack = false" ref="packList" autocomplete="off">
          <select class="toolSelect" size="6" v-show="isFocusedPack" @blur="closeSearchPack()"
                  v-on:mouseenter="isMouseOnPack = true" v-on:mouseleave="isMouseOnPack = false">
            <option v-for="pack in skuPackList" :id="'id' + pack.id" class="groupOptions" v-on:click="selectPack(pack)" :class="{'hoverOption' : pack.hover}"
                    v-on:mouseenter="hoverSelect(skuPackList, pack)">{{pack.text}}</option>
          </select>
          <input class="inputProp" ref="packInput" tabindex="22">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductPackage', $refs.packInput.value, skuPackList, $refs.packList, selectedPack)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductPackage', $refs.packInput.value, skuPackList, $refs.packList, selectedPack)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Квант: </p>
          <input v-on:keydown.down="downSelect(skuQuantityList)" v-on:keydown.up="upSelect(skuQuantityList)" v-on:keydown.35="endSelect(skuQuantityList)" v-on:keydown.36="homeSelect(skuQuantityList)" tabindex="23" v-on:keydown.tab="isFocusedQuantity=false"
                 v-on:keydown.enter="enterSelect(skuQuantityList, 'skuQuantityList')" type="text" class="toolSearch" v-on:input="refreshSkuQuantityList(false)" @focus="openSearchQuantity()" @blur="closeSearchQuantity()"
                 v-on:mouseenter="isMouseOnQuantity = true" v-on:mouseleave="isMouseOnQuantity = false" ref="quantityList" autocomplete="off">
          <select class="toolSelect lowerToolSelect" size="6" v-show="isFocusedQuantity" @blur="closeSearchQuantity()"
                  v-on:mouseenter="isMouseOnQuantity = true" v-on:mouseleave="isMouseOnQuantity = false">
            <option v-for="quantity in skuQuantityList" :id="'id' + quantity.id" class="groupOptions" v-on:click="selectQuantity(quantity)" :class="{'hoverOption' : quantity.hover}"
                    v-on:mouseenter="hoverSelect(skuQuantityList, quantity)">{{quantity.text}}</option>
          </select>
          <input class="inputProp" ref="quantityInput" tabindex="24">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductQuantity', $refs.quantityInput.value, skuQuantityList, $refs.quantityList, selectedQuantity)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductQuantity', $refs.quantityInput.value, skuQuantityList, $refs.quantityList, selectedQuantity)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Производитель: </p>
          <input v-on:keydown.down="downSelect(skuManufacturerList)" v-on:keydown.up="upSelect(skuManufacturerList)" v-on:keydown.35="endSelect(skuManufacturerList)" v-on:keydown.36="homeSelect(skuManufacturerList)" tabindex="25" v-on:keydown.tab="isFocusedManufacturer=false"
                 v-on:keydown.enter="enterSelect(skuManufacturerList, 'skuManufacturerList')" type="text" class="toolSearch" v-on:input="refreshSkuManufacturerList(false)" @focus="openSearchManufacturer()" @blur="closeSearchManufacturer()"
                 v-on:mouseenter="isMouseOnManufacturer = true" v-on:mouseleave="isMouseOnManufacturer = false" ref="manufacturerList" autocomplete="off">
          <select class="toolSelect lowerToolSelect" size="6" v-show="isFocusedManufacturer" @blur="closeSearchManufacturer()"
                  v-on:mouseenter="isMouseOnManufacturer = true" v-on:mouseleave="isMouseOnManufacturer = false">
            <option v-for="manufacturer in skuManufacturerList" :id="'id' + manufacturer.id" class="groupOptions" v-on:click="selectManufacturer(manufacturer)" :class="{'hoverOption' : manufacturer.hover}"
                    v-on:mouseenter="hoverSelect(skuManufacturerList, manufacturer)">{{manufacturer.text}}
            </option>
          </select>
          <input class="inputProp" ref="manufacturerInput" tabindex="26">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductManufacturer', $refs.manufacturerInput.value, skuManufacturerList, $refs.manufacturerList, selectedManufacturer)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductManufacturer', $refs.manufacturerInput.value, skuManufacturerList, $refs.manufacturerList, selectedManufacturer)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Страна производства: </p>
          <input v-on:keydown.down="downSelect(skuOriginList)" v-on:keydown.up="upSelect(skuOriginList)" v-on:keydown.35="endSelect(skuOriginList)" v-on:keydown.36="homeSelect(skuOriginList)" tabindex="27" v-on:keydown.tab="isFocusedOrigin=false"
                 v-on:keydown.enter="enterSelect(skuOriginList, 'skuOriginList')" type="text" class="toolSearch" v-on:input="refreshSkuOriginList(false)" @focus="openSearchOrigin()" @blur="closeSearchOrigin()"
                 v-on:mouseenter="isMouseOnOrigin = true" v-on:mouseleave="isMouseOnOrigin = false" ref="originList" autocomplete="off">
          <select class="toolSelect lowerToolSelect" size="6" v-show="isFocusedOrigin" @blur="closeSearchOrigin()"
                  v-on:mouseenter="isMouseOnOrigin = true" v-on:mouseleave="isMouseOnOrigin = false">
            <option v-for="origin in skuOriginList" :id="'id' + origin.id" class="groupOptions" v-on:click="selectOrigin(origin)" :class="{'hoverOption' : origin.hover}"
                    v-on:mouseenter="hoverSelect(skuOriginList, origin)">{{origin.text}}</option>
          </select>
          <input class="inputProp" ref="originInput" tabindex="28">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductOrigin', $refs.originInput.value, skuOriginList, $refs.originList, selectedOrigin)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductOrigin', $refs.originInput.value, skuOriginList, $refs.originList, selectedOrigin)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>

        <div class="skuPropLabel">
          <p class="label">Вес: </p>
          <input class="mass" ref="weightInput" tabindex="29">
        </div>
        <div class="skuPropLabel">
          <p class="label">Масса: </p>
          <input class="mass" ref="massInput" tabindex="30">
        </div>
        <div class="skuPropLabel">
          <p class="label">% алкоголя: </p>
          <input v-on:keydown.down="downSelect(skuABVList)" v-on:keydown.up="upSelect(skuABVList)" v-on:keydown.35="endSelect(skuABVList)" v-on:keydown.36="homeSelect(skuABVList)" tabindex="31" v-on:keydown.tab="isFocusedABV=false"
                 v-on:keydown.enter="enterSelect(skuABVList, 'skuABVList')" type="text" class="toolSearch" v-on:input="refreshSkuABVList(false)" @focus="openSearchABV()" @blur="closeSearchABV()"
                 v-on:mouseenter="isMouseOnABV = true" v-on:mouseleave="isMouseOnABV = false" ref="abvList" autocomplete="off">
          <select class="toolSelect lowerToolSelect" size="6" v-show="isFocusedABV" @blur="closeSearchABV()"
                  v-on:mouseenter="isMouseOnABV = true" v-on:mouseleave="isMouseOnABV = false">
            <option v-for="abv in skuABVList" :id="'id' + abv.id" class="groupOptions" v-on:click="selectABV(abv)" :class="{'hoverOption' : abv.hover}"
                    v-on:mouseenter="hoverSelect(skuABVList, abv)">{{abv.text}}</option>
          </select>
          <input class="inputProp" ref="abvInput" tabindex="32">
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="changeProperty('ProductABV', $refs.abvInput.value, skuABVList, $refs.abvList, selectedABV)">
            <v-icon>mdi-wrench</v-icon>
          </v-btn>
          <v-btn x-small icon color="black" class="buttonsProp"
                 v-on:click="addProperty('ProductABV', $refs.abvInput.value, skuABVList, $refs.abvList, selectedABV)">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>
        <div class="skuPropLabel" style="height: 30px; margin-bottom: 10px">
          <p class="label"></p>
          <button class="buttonChange" v-on:click="updateSku()" :disabled="isReconnectedChangeGoodProductWS">Изменить</button>
          <button class="buttonCreate" v-on:click="createNewSku()" :disabled="isReconnectedGoodProductWS || isAddingSku">Создать</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../product-api'
import axios from 'axios'
import {
  isConnectedGoodProduct,
  addHandlerGoodProduct,
  connectGoodProduct, disconnectGoodProduct,
  sendGoodProduct, getStomp
} from "@/newProductWS";
import {
  addHandlerChangeGoodProduct,
  connectChangeGoodProduct, disconnectChangeGoodProduct,
  sendChangeGoodProduct, isConnectedChangeGoodProduct
} from "@/changeProductWS";
import {
  addHandlerConfirmProduct,
  connectConfirmProduct, disconnectConfirmGoodProduct,
  sendConfirmProduct, isConnectedConfirmGoodProduct
} from "@/confirmProductWS";

export default {
  data() {
    return {
      isFocusOnRawSkuList: false,
      isFocusOnGoodSkuList: false,

      isConfirmingSku: false,
      isAddingSku: false,

      busyGood: true,
      busyRaw: true,

      rawSkuSkipCount: 0,
      goodSkuSkipCount: 0,

      isMouseMode: true,

      isMouseOnGroup: false,
      isMouseOnClientGroup: false,
      isMouseOnSubGroup: false,
      isMouseOnType: false,
      isMouseOnGoods: false,
      isMouseOnBrand: false,
      isMouseOnSubBrand: false,
      isMouseOnColor: false,
      isMouseOnFlavor: false,
      isMouseOnExtra: false,
      isMouseOnPack: false,
      isMouseOnQuantity: false,
      isMouseOnManufacturer: false,
      isMouseOnOrigin: false,
      isMouseOnABV: false,


      isFocusedGroup: false,
      isFocusedClientGroup: false,
      isFocusedSubGroup: false,
      isFocusedType: false,
      isFocusedGoods: false,
      isFocusedBrand: false,
      isFocusedSubBrand: false,
      isFocusedColor: false,
      isFocusedFlavor: false,
      isFocusedExtra: false,
      isFocusedPack: false,
      isFocusedQuantity: false,
      isFocusedManufacturer: false,
      isFocusedOrigin: false,
      isFocusedABV: false,

      isOpeningGroup: false,
      isOpeningClientGroup: false,
      isOpeningSubGroup: false,
      isOpeningType: false,
      isOpeningGoods: false,
      isOpeningBrand: false,
      isOpeningSubBrand: false,
      isOpeningColor: false,
      isOpeningFlavor: false,
      isOpeningExtra: false,
      isOpeningPack: false,
      isOpeningQuantity: false,
      isOpeningManufacturer: false,
      isOpeningOrigin: false,
      isOpeningABV: false,

      filteredRawCounter: 0,
      rawCounter: 0,

      selectedGroup: '',
      selectedClientGroup: '',
      selectedSubGroup: '',
      selectedType: '',
      selectedGoods: '',
      selectedBrand: '',
      selectedSubBrand: '',
      selectedColor: '',
      selectedFlavor: '',
      selectedExtra: '',
      selectedPack: '',
      selectedQuantity: '',
      selectedManufacturer: '',
      selectedOrigin: '',
      selectedABV: '',
      selectedWeight: '',
      selectedMass: '',


      skuGroupList: '',
      skuClientGroupList: '',
      skuSubGroupList: '',
      skuTypeList: '',
      skuGoodsList: '',
      skuBrandList: '',
      skuSubBrandList: '',
      skuColorList: '',
      skuFlavorList: '',
      skuExtraList: '',
      skuPackList: '',
      skuQuantityList: '',
      skuManufacturerList: '',
      skuOriginList: '',
      skuABVList: '',

      currentGoodId: '',
      goodSkuList: [],
      rawSkuList: [],
      errors: [],
      currentGoodSku: '',

      selectedGoodSku: '',
      selectedRawSku: '',

      sortRawKey: '',
      reverseRaw: false,

      sortGoodKey: 'sku',
      reverseGood: true,

      rawFilterInput: '',
      goodFilterInput: '',

      employeeFilterInput: '',
      attributeFilterInput: '',

      productRawGroupList: '',
      selectedClass: '',

      rawSkuCancelToken: null,
      goodSkuCancelToken: null,

      rawSkuScrollingCancelToken: null,
      goodSkuScrolloingCancelToken: null,

      getCounterCancelToken: null,

      startXRawId: '',
      startWidthRawId: '',
      startHeightRawId: '',

      startXRawSku: '',
      startWidthRawSku: '',
      startHeightRawSku: '',

      startXGoodId: '',
      startWidthGoodId: '',
      startHeightGoodId: '',

      isWithoutConfirm: false,

      lastClick: '',

      timeout: 0,

      waitingGoodSku: '',
      waitingRawSku: 0,

      isReconnectedGoodProductWS: false,
      isReconnectedChangeGoodProductWS: false,
      isReconnectedConfirmGoodProductWS: false,
    }
  },

  methods: {
    /*****---ABV---*****/
    refreshSkuABVList(isFirstSelect) {
      let filter = this.$refs.abvList.value;
      api.getABVList(isFirstSelect ? filter : this.$refs.abvList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuABVList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuABVList.length; i++) {
            this.skuABVList[i].hover = false;
            if (this.skuABVList[i].text === filter) {
              this.skuABVList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuABVList, this.skuABVList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningABV)
            this.isFocusedABV = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectABV(abv) {
      this.selectedABV = abv;
      this.isFocusedABV = false;
    },
    closeSearchABV() {
      if (!this.isMouseOnABV) {
        this.isOpeningABV = false;
        this.$refs.abvList.value = this.selectedABV.text;
        this.$refs.abvInput.value = this.selectedABV.text;
        this.isFocusedABV = false;
      }
    },
    openSearchABV() {
      this.isOpeningABV = true;
      this.refreshSkuABVList(true);
      this.$refs.abvList.value = '';
    },
    /**--**/


    /*****---Origin---*****/
    refreshSkuOriginList(isFirstSelect) {
      let filter = this.$refs.originList.value;
      api.getOriginList(isFirstSelect ? filter : this.$refs.originList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuOriginList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuOriginList.length; i++) {
            this.skuOriginList[i].hover = false;
            if (this.skuOriginList[i].text === filter) {
              this.skuOriginList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuOriginList, this.skuOriginList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningOrigin)
            this.isFocusedOrigin = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectOrigin(origin) {
      this.selectedOrigin = origin;
      this.isFocusedOrigin = false;
    },
    closeSearchOrigin() {
      if (!this.isMouseOnOrigin) {
        this.isOpeningOrigin = false;
        this.$refs.originList.value = this.selectedOrigin.text;
        this.$refs.originInput.value = this.selectedOrigin.text;
        this.isFocusedOrigin = false;
      }
    },
    openSearchOrigin() {
      this.isOpeningOrigin = true;
      this.refreshSkuOriginList(true);
      this.$refs.originList.value = '';
    },
    /**--**/


    /*****---Manufacturer---*****/
    refreshSkuManufacturerList(isFirstSelect) {
      let filter = this.$refs.manufacturerList.value;
      api.getManufacturerList(isFirstSelect ? filter : this.$refs.manufacturerList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuManufacturerList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuManufacturerList.length; i++) {
            this.skuManufacturerList[i].hover = false;
            if (this.skuManufacturerList[i].text === filter) {
              this.skuManufacturerList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuManufacturerList, this.skuManufacturerList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningManufacturer)
            this.isFocusedManufacturer = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectManufacturer(manufacturer) {
      this.selectedManufacturer = manufacturer;
      this.isFocusedManufacturer = false;
    },
    closeSearchManufacturer() {
      if (!this.isMouseOnManufacturer) {
        this.isOpeningManufacturer = false;
        this.$refs.manufacturerList.value = this.selectedManufacturer.text;
        this.$refs.manufacturerInput.value = this.selectedManufacturer.text;
        this.isFocusedManufacturer = false;
      }
    },
    openSearchManufacturer() {
      this.isFocusedManufacturer = true;
      this.refreshSkuManufacturerList(true);
      this.$refs.manufacturerList.value = '';
    },
    /**--**/

    /*****---Quantity---*****/
    refreshSkuQuantityList(isFirstSelect) {
      let filter = this.$refs.quantityList.value
      api.getQuantityList(isFirstSelect ? filter : this.$refs.quantityList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuQuantityList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuQuantityList.length; i++) {
            this.skuQuantityList[i].hover = false;
            if (this.skuQuantityList[i].text === filter) {
              this.skuQuantityList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuQuantityList, this.skuQuantityList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningQuantity)
            this.isFocusedQuantity = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectQuantity(quantity) {
      this.selectedQuantity = quantity;
      this.isFocusedQuantity = false;
    },
    closeSearchQuantity() {
      if (!this.isMouseOnQuantity) {
        this.isOpeningQuantity = false;
        this.$refs.quantityList.value = this.selectedQuantity.text;
        this.$refs.quantityInput.value = this.selectedQuantity.text;
        this.isFocusedQuantity = false;
      }
    },
    openSearchQuantity() {
      this.isOpeningQuantity = true;
      this.refreshSkuQuantityList(true);
      this.$refs.quantityList.value = '';
    },
    /**--**/


    /*****---Pack---*****/
    refreshSkuPackList(isFirstSelect) {
      let filter = this.$refs.packList.value;
      api.getPackageList(isFirstSelect ? filter : this.$refs.packList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuPackList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuPackList.length; i++) {
            this.skuPackList[i].hover = false;
            if (this.skuPackList[i].text === filter) {
              this.skuPackList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuPackList, this.skuPackList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningPack)
            this.isFocusedPack = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectPack(pack) {
      this.selectedPack = pack;
      this.isFocusedPack = false;
    },
    closeSearchPack() {
      if (!this.isMouseOnPack) {
        this.isOpeningPack = false;
        this.$refs.packList.value = this.selectedPack.text;
        this.$refs.packInput.value = this.selectedPack.text;
        this.isFocusedPack = false;
      }
    },
    openSearchPack() {
      this.isFocusedPack = true;
      this.refreshSkuPackList(true);
      this.$refs.packList.value = '';
    },
    /**--**/

    /*****---Extra---*****/
    refreshSkuExtraList(isFirstSelect) {
      let filter = this.$refs.extraList.value;
      api.getExtraList(isFirstSelect ? filter : this.$refs.extraList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuExtraList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuExtraList.length; i++) {
            this.skuExtraList[i].hover = false;
            if (this.skuExtraList[i].text === filter) {
              this.skuExtraList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuExtraList, this.skuExtraList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningExtra)
            this.isFocusedExtra = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectExtra(extra) {
      this.selectedExtra = extra;
      this.isFocusedExtra = false;
    },
    closeSearchExtra() {
      if (!this.isMouseOnExtra) {
        this.isOpeningExtra = false;
        this.$refs.extraList.value = this.selectedExtra.text;
        this.$refs.extraInput.value = this.selectedExtra.text;
        this.isFocusedExtra = false;
      }
    },
    openSearchExtra() {
      this.isOpeningExtra = true;
      this.refreshSkuExtraList(true);
      this.$refs.extraList.value = '';
    },
    /**--**/

    /*****---Flavor---*****/
    refreshSkuFlavorList(isFirstSelect) {
      let filter = this.$refs.flavorList.value;
      api.getFlavorList(isFirstSelect ? filter : this.$refs.flavorList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuFlavorList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuFlavorList.length; i++) {
            this.skuFlavorList[i].hover = false;
            if (this.skuFlavorList[i].text === filter) {
              this.skuFlavorList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuFlavorList, this.skuFlavorList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningFlavor)
            this.isFocusedFlavor = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectFlavor(flavor) {
      this.selectedFlavor = flavor;
      this.isFocusedFlavor = false;
    },
    closeSearchFlavor() {
      if (!this.isMouseOnFlavor) {
        this.isOpeningFlavor = false;
        this.$refs.flavorList.value = this.selectedFlavor.text;
        this.$refs.flavorInput.value = this.selectedFlavor.text;
        this.isFocusedFlavor = false;
      }
    },
    openSearchFlavor() {
      this.isOpeningFlavor = true;
      this.refreshSkuFlavorList(true);
      this.$refs.flavorList.value = '';
    },
    /**--**/

    /*****---Color---*****/
    refreshSkuColorList(isFirstSelect) {
      let filter = this.$refs.colorList.value;
      api.getColorList(isFirstSelect ? filter : this.$refs.colorList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuColorList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuColorList.length; i++) {
            this.skuColorList[i].hover = false;
            if (this.skuColorList[i].text === filter) {
              this.skuColorList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuColorList, this.skuColorList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningColor)
            this.isFocusedColor = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectColor(color) {
      this.selectedColor = color;
      this.isFocusedColor = false;
    },
    closeSearchColor() {
      if (!this.isMouseOnColor) {
        this.isOpeningColor = false;
        this.$refs.colorList.value = this.selectedColor.text;
        this.$refs.colorInput.value = this.selectedColor.text;
        this.isFocusedColor = false;
      }
    },
    openSearchColor() {
      this.isOpeningColor = true;
      this.refreshSkuColorList(true);
      this.$refs.colorList.value = '';
    },
    /**--**/

    /*****---SubBrand---*****/
    refreshSkuSubBrandList(isFirstSelect) {
      let filter = this.$refs.subBrandList.value;
      api.getSubBrandList(isFirstSelect ? filter : this.$refs.subBrandList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuSubBrandList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuSubBrandList.length; i++) {
            this.skuSubBrandList[i].hover = false;
            if (this.skuSubBrandList[i].text === filter) {
              this.skuSubBrandList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuSubBrandList, this.skuSubBrandList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningSubBrand)
            this.isFocusedSubBrand = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectSubBrand(subBrand) {
      this.selectedSubBrand = subBrand;
      this.isFocusedSubBrand = false;
    },
    closeSearchSubBrand() {
      if (!this.isMouseOnSubBrand) {
        this.isOpeningSubBrand = false;
        this.$refs.subBrandList.value = this.selectedSubBrand.text;
        this.$refs.subBrandInput.value = this.selectedSubBrand.text;
        this.isFocusedSubBrand = false;
      }
    },
    openSearchSubBrand() {
      this.refreshSkuSubBrandList(true);
      this.$refs.subBrandList.value = '';
      this.isOpeningSubBrand = true;
    },
    /**--**/

    /*****---Brand---*****/
    refreshSkuBrandList(isFirstSelect) {
      let filter = this.$refs.brandList.value
      api.getBrandList(isFirstSelect ? filter : this.$refs.brandList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuBrandList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuBrandList.length; i++) {
            this.skuBrandList[i].hover = false;
            if (this.skuBrandList[i].text === filter) {
              this.skuBrandList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuBrandList, this.skuBrandList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningBrand)
            this.isFocusedBrand = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectBrand(brand) {
      this.selectedBrand = brand;
      this.isFocusedBrand = false;
    },
    closeSearchBrand() {
      if (!this.isMouseOnBrand) {
        this.isOpeningBrand = false;
        this.$refs.brandList.value = this.selectedBrand.text;
        this.$refs.brandInput.value = this.selectedBrand.text;
        this.isFocusedBrand = false;
      }
    },
    openSearchBrand() {
      this.isOpeningBrand = true;
      this.refreshSkuBrandList(true);
      this.$refs.brandList.value = '';
    },
    /**--**/


    /*****---Goods---*****/
    refreshSkuGoodsList(isFirstSelect) {
      let filter = this.$refs.goodsList.value;
      api.getGoodsList(isFirstSelect ? filter : this.$refs.goodsList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuGoodsList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuGoodsList.length; i++) {
            this.skuGoodsList[i].hover = false;
            if (this.skuGoodsList[i].text === filter) {
              this.skuGoodsList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuGoodsList, this.skuGoodsList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningGoods)
            this.isFocusedGoods = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectGoods(goods) {
      this.selectedGoods = goods;
      this.isFocusedGoods = false;
    },
    closeSearchGoods() {
      if (!this.isMouseOnGoods) {
        this.isOpeningGoods = false;
        this.$refs.goodsList.value = this.selectedGoods.text;
        this.$refs.goodsInput.value = this.selectedGoods.text;
        this.isFocusedGoods = false;
      }
    },
    openSearchGoods() {
      this.isOpeningGoods = true;
      this.refreshSkuGoodsList(true);
      this.$refs.goodsList.value = '';
    },
    /**--**/

    /*****---Group---*****/
    refreshSkuGroupList(isFirstSelect) {
      let filter = this.$refs.groupList.value;
      api.getGroupList(isFirstSelect ? filter : this.$refs.groupList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuGroupList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuGroupList.length; i++) {
            this.skuGroupList[i].hover = false;
            if (this.skuGroupList[i].text === filter) {
              this.skuGroupList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuGroupList, this.skuGroupList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningGroup)
            this.isFocusedGroup = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectGroup(group) {
      this.selectedGroup = group;
      this.isFocusedGroup = false;
    },
    closeSearchGroup() {
      if (!this.isMouseOnGroup) {
        this.isOpeningGroup = false;
        this.$refs.groupList.value = this.selectedGroup.text;
        this.$refs.groupInput.value = this.selectedGroup.text;
        this.isFocusedGroup = false;
      }
    },
    openSearchGroup() {
      this.isOpeningGroup = true;
      this.refreshSkuGroupList(true);
      this.$refs.groupList.value = '';
    },
    /**--**/

    /*****---ClientGroup---*****/
    refreshSkuClientGroupList(isFirstSelect) {
      let filter = this.$refs.clientGroupList.value;
      api.getClientGroupList(isFirstSelect ? filter : this.$refs.clientGroupList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuClientGroupList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuClientGroupList.length; i++) {
            this.skuClientGroupList[i].hover = false;
            if (this.skuClientGroupList[i].text === filter) {
              this.skuClientGroupList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuClientGroupList, this.skuClientGroupList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningClientGroup)
            this.isOpeningClientGroup = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectClientGroup(clientGroup) {
      this.selectedClientGroup = clientGroup;
      this.isFocusedClientGroup = false;
    },
    closeSearchClientGroup() {
      if (!this.isMouseOnClientGroup) {
        this.isOpeningClientGroup = false;
        this.$refs.clientGroupList.value = this.selectedClientGroup.text;
        this.$refs.clientGroupInput.value = this.selectedClientGroup.text;
        this.isFocusedClientGroup = false;
      }
    },
    openSearchClientGroup() {
      this.isOpeningClientGroup = true;
      this.refreshSkuClientGroupList(true);
      this.$refs.clientGroupList.value = '';
      this.isFocusedClientGroup = true;
    },
    /**--**/

    /*****---SubGroup---*****/
    refreshSkuSubGroupList(isFirstSelect) {
      let filter = this.$refs.subGroupList.value;
      api.getSubGroupList(isFirstSelect ? filter : this.$refs.subGroupList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuSubGroupList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuSubGroupList.length; i++) {
            this.skuSubGroupList[i].hover = false;
            if (this.skuSubGroupList[i].text === filter) {
              this.skuSubGroupList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuSubGroupList, this.skuSubGroupList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningSubGroup)
            this.isFocusedSubGroup = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectSubGroup(subGroup) {
      this.selectedSubGroup = subGroup;
      this.isFocusedSubGroup = false;
    },
    closeSearchSubGroup() {
      if (!this.isMouseOnSubGroup) {
        this.isOpeningSubGroup = false;
        this.$refs.subGroupList.value = this.selectedSubGroup.text;
        this.$refs.subGroupInput.value = this.selectedSubGroup.text;
        this.isFocusedSubGroup = false;
      }
    },
    openSearchSubGroup() {
      this.isOpeningSubGroup = true;
      this.refreshSkuSubGroupList(true);
      this.$refs.subGroupList.value = '';
    },
    /**--**/


    /*****---Type---*****/
    refreshSkuTypeList(isFirstSelect) {
      let filter = this.$refs.typeList.value;
      api.getTypeList(isFirstSelect ? filter : this.$refs.typeList.value, this.selectedClass.flag, isFirstSelect).then(response => {
        this.skuTypeList = response.data;
        if (isFirstSelect) {
          for (let i = 0; i < this.skuTypeList.length; i++) {
            this.skuTypeList[i].hover = false;
            if (this.skuTypeList[i].text === filter) {
              this.skuTypeList[i].hover = true;
              this.$nextTick(function () {
                this.moveDown(this.skuTypeList, this.skuTypeList[i].id, 'start');
              })
            }
          }
          if (this.isOpeningType)
            this.isFocusedType = true;
        } else {
          let activeSelect = document.activeElement.nextSibling;
          activeSelect.scrollTo(0, 0);
        }
      }).catch(e => {
        this.errors.push(e);
      })
    },
    selectType(type) {
      this.selectedType = type;
      this.isFocusedType = false;
    },
    closeSearchType() {
      if (!this.isMouseOnType) {
        this.isOpeningType = false;
        this.$refs.typeList.value = this.selectedType.text;
        this.$refs.typeInput.value = this.selectedType.text;
        this.isFocusedType = false;
      }
    },
    openSearchType() {
      this.isOpeningType = true;
      this.refreshSkuTypeList(true);
      this.$refs.typeList.value = '';
    },
    /**--**/


    /*****---TableSelects---*****/
    selectRawRow(sku) {
      this.selectedRawSku.hover = false;
      sku.hover = true;
      this.selectedRawSku = sku;
    },
    selectGoodRow(sku) {
      if (this.selectedGoodSku !== undefined) {
        this.selectedGoodSku.hover = false;
      }
      sku.hover = true;
      this.selectedGoodSku = sku;
      console.log(this.selectedGoodSku);
      this.getPropsSku(this.selectedGoodSku.id);
      console.log('skuSelected');
    },
    /**--**/


    getPropsSku(id) {
      api.getGoodSku(id).then(response => {

        this.currentGoodSku = response.data;

        this.selectedGroup = {id: this.currentGoodSku.groupID, text: this.currentGoodSku.group}
        this.$refs.groupList.value = this.selectedGroup.text;
        this.$refs.groupInput.value = this.selectedGroup.text;

        this.selectedClientGroup = {id: this.currentGoodSku.clientGroupID, text: this.currentGoodSku.clientGroup}
        this.$refs.clientGroupList.value = this.selectedClientGroup.text;
        this.$refs.clientGroupInput.value = this.selectedClientGroup.text;

        this.selectedSubGroup = {id: this.currentGoodSku.subGroupID, text: this.currentGoodSku.subGroup}
        this.$refs.subGroupList.value = this.selectedSubGroup.text;
        this.$refs.subGroupInput.value = this.selectedSubGroup.text;

        this.selectedType = {id: this.currentGoodSku.typeID, text: this.currentGoodSku.type}
        this.$refs.typeList.value = this.selectedType.text;
        this.$refs.typeInput.value = this.selectedType.text;

        this.selectedGoods = {id: this.currentGoodSku.goodsID, text: this.currentGoodSku.goods}
        this.$refs.goodsList.value = this.selectedGoods.text;
        this.$refs.goodsInput.value = this.selectedGoods.text;

        this.selectedBrand = {id: this.currentGoodSku.brandID, text: this.currentGoodSku.brand}
        this.$refs.brandList.value = this.selectedBrand.text;
        this.$refs.brandInput.value = this.selectedBrand.text;

        this.selectedSubBrand = {id: this.currentGoodSku.subBrandID, text: this.currentGoodSku.subBrand}
        this.$refs.subBrandList.value = this.selectedSubBrand.text;
        this.$refs.subBrandInput.value = this.selectedSubBrand.text;

        this.selectedColor = {id: this.currentGoodSku.colorID, text: this.currentGoodSku.color}
        this.$refs.colorList.value = this.selectedColor.text;
        this.$refs.colorInput.value = this.selectedColor.text;

        this.selectedFlavor = {id: this.currentGoodSku.flavorID, text: this.currentGoodSku.flavor}
        this.$refs.flavorList.value = this.selectedFlavor.text;
        this.$refs.flavorInput.value = this.selectedFlavor.text;

        this.selectedExtra = {id: this.currentGoodSku.extraID, text: this.currentGoodSku.extra}
        this.$refs.extraList.value = this.selectedExtra.text;
        this.$refs.extraInput.value = this.selectedExtra.text;

        this.selectedPack = {id: this.currentGoodSku.packageID, text: this.currentGoodSku.pack}
        this.$refs.packList.value = this.selectedPack.text;
        this.$refs.packInput.value = this.selectedPack.text;

        this.selectedQuantity = {id: this.currentGoodSku.quantityID, text: this.currentGoodSku.quantity}
        this.$refs.quantityList.value = this.selectedQuantity.text;
        this.$refs.quantityInput.value = this.selectedQuantity.text;

        this.selectedManufacturer = {id: this.currentGoodSku.manufacturerID, text: this.currentGoodSku.manufacturer}
        this.$refs.manufacturerList.value = this.selectedManufacturer.text;
        this.$refs.manufacturerInput.value = this.selectedManufacturer.text;

        this.selectedOrigin = {id: this.currentGoodSku.originID, text: this.currentGoodSku.origin}
        this.$refs.originList.value = this.selectedOrigin.text;
        this.$refs.originInput.value = this.selectedOrigin.text;

        this.selectedExtra = {id: this.currentGoodSku.extraID, text: this.currentGoodSku.extra}
        this.$refs.extraList.value = this.selectedExtra.text;
        this.$refs.extraInput.value = this.selectedExtra.text;

        this.selectedABV = {id: this.currentGoodSku.abvID, text: this.currentGoodSku.abv}
        this.$refs.abvList.value = this.selectedABV.text;
        this.$refs.abvInput.value = this.selectedABV.text;

        this.selectedWeight = {text: this.currentGoodSku.weight}
        this.$refs.weightInput.value = this.selectedWeight.text;

        this.selectedMass = {text: this.currentGoodSku.mass}
        this.$refs.massInput.value = this.selectedMass.text;
      }).catch(e => {
        console.log(e);
        if (!e.contains('Cancel'))
          alert(e);
      })
    },
    refreshGood(filter) {
      this.goodSkuSkipCount = 0;
      if (this.goodSkuCancelToken) {
        this.goodSkuCancelToken.cancel('Cancel');
      }
      if (this.goodSkuScrolloingCancelToken) {
        this.goodSkuScrolloingCancelToken.cancel('Cancel');
      }
      this.goodSkuCancelToken = axios.CancelToken.source();
      api.refreshGoodSKU(filter, this.selectedClass.id, this.attributeFilterInput, this.goodSkuCancelToken, 5000, this.goodSkuSkipCount).then(response => {
        this.goodSkuList = response.data;
        this.selectedGoodSku = this.goodSkuList[0];
        this.getPropsSku(this.selectedGoodSku.id);
        this.goodSkuCancelToken = null;
        this.busyGood = false;
      }).catch(e => {
        this.errors.push(e);
      })
    },
    refreshRaw(filter) {
      this.rawSkuSkipCount = 0;
      if (this.rawSkuCancelToken) {
        this.rawSkuCancelToken.cancel('Cancel');
      }
      if (this.rawSkuScrollingCancelToken) {
        this.rawSkuScrollingCancelToken.cancel('Cancel');
      }
      this.rawSkuCancelToken = axios.CancelToken.source();
      api.refreshRawSKU(filter, this.selectedClass.id, this.isWithoutConfirm, this.employeeFilterInput, this.rawSkuCancelToken, 5000, this.rawSkuSkipCount).then(response => {
        this.rawSkuList = response.data;
        this.selectedRawSku = this.rawSkuList[0];
        this.rawSkuCancelToken = null;
        this.busyRaw = false;
      }).catch(e => {
        this.errors.push(e);
      })
      this.getCounters();
    },

    refreshProductClass() {
      api.getClassList().then(response => {
        this.productRawGroupList = response.data;
        this.selectedClass = this.productRawGroupList[0];
      }).catch(e => {
        this.errors.push(e);
      })
    },

    changeProductClass() {
      this.productRawGroupList.forEach(e => {
        if (e.text === this.$refs.selectedRawGroup.value) {
          this.selectedClass = e
        }
      })
      this.refreshGood('');
      this.refreshRaw('');
      this.refreshSkuGroupList(false);
      this.refreshSkuClientGroupList(false);
      this.refreshSkuSubGroupList(false);
      this.refreshSkuTypeList(false);
      this.refreshSkuGoodsList(false);
      this.refreshSkuBrandList(false);
      this.refreshSkuSubBrandList(false);
      this.refreshSkuColorList(false);
      this.refreshSkuFlavorList(false);
      this.refreshSkuExtraList(false);
      this.refreshSkuPackList(false);
      this.refreshSkuQuantityList(false);
      this.refreshSkuManufacturerList(false);
      this.refreshSkuOriginList(false);
      this.refreshSkuABVList(false);
    },

    dynamicSort(property) {
      var sortOrder = 1;
      if (property[0] === "-") {
        sortOrder = -1;
        property = property.substr(1);
      }
      return function (a, b) {
        var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
        return result * sortOrder;
      }
    },

    sortRawBy(sortKey) {
      if (sortKey === this.sortRawKey && this.reverseRaw) {
        this.rawSkuList.sort(this.dynamicSort('-' + sortKey));
        this.reverseRaw = false;
      } else {
        this.rawSkuList.sort(this.dynamicSort(sortKey));
        this.reverseRaw = true;
      }
      this.sortRawKey = sortKey;
    },

    sortGoodBy(sortKey) {
      if (sortKey === this.sortGoodKey && this.reverseGood) {
        this.goodSkuList.sort(this.dynamicSort('-' + sortKey));
        this.reverseGood = false;
      } else {
        this.goodSkuList.sort(this.dynamicSort(sortKey));
        this.reverseGood = true;
      }
      this.sortGoodKey = sortKey;
    },

    columnResizableRawId() {
      var c = this.$refs.resId;
      c.addEventListener('mousedown', this.initDragRawId, false);
    },

    columnResizableRawSku() {
      var c = this.$refs.resRawSku;
      c.addEventListener('mousedown', this.initDragRawSku, false);
    },

    columnResizableGoodId() {
      var c = this.$refs.resGoodId;
      c.addEventListener('mousedown', this.initDragGoodId, false);
    },


    initDragRawId(e) {
      this.startXRawId = e.clientX;
      this.startWidthRawId = parseInt(document.defaultView.getComputedStyle(this.$refs.thId).width, 10);
      document.documentElement.addEventListener('mousemove', this.doDragRawId, false);
      document.documentElement.addEventListener('mouseup', this.stopDragRawId, false);
    },

    initDragRawSku(e) {
      this.startXRawSku = e.clientX;
      this.startWidthRawSku = parseInt(document.defaultView.getComputedStyle(this.$refs.thRawSku).width, 10);
      document.documentElement.addEventListener('mousemove', this.doDragRawSku, false);
      document.documentElement.addEventListener('mouseup', this.stopDragRawSku, false);
    },

    initDragGoodId(e) {
      this.startXGoodId = e.clientX;
      this.startWidthGoodId = parseInt(document.defaultView.getComputedStyle(this.$refs.thGoodId).width, 10);
      document.documentElement.addEventListener('mousemove', this.doDragGoodId, false);
      document.documentElement.addEventListener('mouseup', this.stopDragGoodId, false);
    },

    doDragRawId(e) {
      this.$refs.thId.style.width = (this.startWidthRawId + e.clientX - this.startXRawId) + 'px';
    },

    doDragRawSku(e) {
      this.$refs.thRawSku.style.width = (this.startWidthRawSku + e.clientX - this.startXRawSku) + 'px';
    },

    doDragGoodId(e) {
      this.$refs.thGoodId.style.width = (this.startWidthGoodId + e.clientX - this.startXGoodId) + 'px';
    },

    stopDragRawId(e) {
      document.documentElement.removeEventListener('mousemove', this.doDragRawId, false);
      document.documentElement.removeEventListener('mouseup', this.stopDragRawId, false);
    },

    stopDragRawSku(e) {
      document.documentElement.removeEventListener('mousemove', this.doDragRawSku, false);
      document.documentElement.removeEventListener('mouseup', this.stopDragRawSku, false);
    },

    stopDragGoodId(e) {
      document.documentElement.removeEventListener('mousemove', this.doDragGoodId, false);
      document.documentElement.removeEventListener('mouseup', this.stopDragGoodId, false);
    },
    createNewSku() {
      this.isAddingSku = true;
      var skuName = '';

      if (this.selectedGoods.text.length > 0)
        skuName = skuName + this.selectedGoods.text;

      if (this.selectedBrand.text.length > 0)
        skuName = skuName + ' ' + this.selectedBrand.text;

      if (this.selectedSubBrand.text.length > 0)
        skuName = skuName + ' ' + this.selectedSubBrand.text;

      if (this.selectedFlavor.text.length > 0)
        skuName = skuName + ' ' + this.selectedFlavor.text;

      if (this.selectedColor.text.length > 0)
        skuName = skuName + ' ' + this.selectedColor.text;

      if (this.selectedABV.text.length > 0)
        skuName = skuName + ' ' + this.selectedABV.text;

      if (this.$refs.weightInput.value.length > 0)
        skuName = skuName + ' ' + this.$refs.weightInput.value;

      if (this.selectedQuantity.text.length > 0)
        skuName = skuName + ' ' + this.selectedQuantity.text;

      if (this.selectedPack.text.length > 0)
        skuName = skuName + ' ' + this.selectedPack.text;

      if (this.selectedOrigin.text.length > 0)
        skuName = skuName + ' ' + this.selectedOrigin.text;

      if (this.selectedExtra.text.length > 0)
        skuName = skuName + ' ' + this.selectedExtra.text;

      skuName.replace('  ', ' ').trim();


      var product = {
        sku: skuName,
        productGroupID: this.selectedGroup.id,
        productSubGroupID: this.selectedSubGroup.id,
        productTypeID: this.selectedType.id,
        productBrandID: this.selectedBrand.id,
        productSubBrandID: this.selectedSubBrand.id,
        productFlavourID: this.selectedFlavor.id,
        productPackageID: this.selectedPack.id,
        productQuantityID: this.selectedQuantity.id,
        productManufacturerID: this.selectedManufacturer.id,
        productOriginID: this.selectedOrigin.id,
        weight: this.$refs.weightInput.value,
        mass: this.$refs.massInput.value.replace(',', '.'),
        productGoodID: this.selectedGoods.id,
        productColourID: this.selectedColor.id,
        productExtraID: this.selectedExtra.id,
        productABVID: this.selectedABV.id,
        productClientGroupID: this.selectedClientGroup.id,
        author: this.$store.getters.getAssignee,
        classId: this.selectedClass.id
      }
      this.waitingGoodSku = skuName;
      console.log('waiting sku: ' + skuName);
      sendGoodProduct(product)
    },

    moveToGood(sku) {
      let tableGoodSku = document.querySelector('#tableGoodSku');
      let currentEl = tableGoodSku.querySelector('#id' + sku.id);
      var targetPosition = {
            top: window.pageYOffset + currentEl.getBoundingClientRect().top,
            bottom: window.pageYOffset + currentEl.getBoundingClientRect().bottom
          },
          // Получаем позиции окна
          windowPosition = {
            top: window.pageYOffset + tableGoodSku.getBoundingClientRect().top + 40,
            bottom: window.pageYOffset + tableGoodSku.getBoundingClientRect().bottom - 20
          };
      if (targetPosition.bottom < windowPosition.top) {
        tableGoodSku.scrollBy(0, 30);
      }
      if (targetPosition.bottom < windowPosition.top || targetPosition.top > windowPosition.bottom) {
        currentEl.scrollIntoView({alignToTop: "true", behavior: "auto", block: "nearest"});
        if (targetPosition.bottom < windowPosition.top) { // Если позиция нижней части элемента больше позиции верхней чайти окна, то элемент виден сверху
          tableGoodSku.scrollBy(0, -25);
        }
      }
    },

    moveToRaw(sku) {
      this.selectRawRow(sku);
      let pageRawSku = document.querySelector('#pageRawSku');
      let currentEl = pageRawSku.querySelector('#id' + sku.id);
      var targetPosition = {
            top: window.pageYOffset + currentEl.getBoundingClientRect().top,
            bottom: window.pageYOffset + currentEl.getBoundingClientRect().bottom
          },
          // Получаем позиции окна
          windowPosition = {
            top: window.pageYOffset + pageRawSku.getBoundingClientRect().top + 50,
            bottom: window.pageYOffset + pageRawSku.getBoundingClientRect().bottom - 20
          };
      if (targetPosition.bottom < windowPosition.top) {
        pageRawSku.scrollBy(0, 30);
      }
      if (targetPosition.bottom < windowPosition.top || targetPosition.top > windowPosition.bottom) {
        currentEl.scrollIntoView({alignToTop: "true", behavior: "auto", block: "nearest"});
        if (targetPosition.bottom < windowPosition.top) { // Если позиция нижней части элемента больше позиции верхней чайти окна, то элемент виден сверху
          pageRawSku.scrollBy(0, -25);
        }
      }
    },

    updateSku() {
      var skuName = '';

      if (this.selectedGoods.text.length > 0)
        skuName = skuName + this.selectedGoods.text;

      if (this.selectedBrand.text.length > 0)
        skuName = skuName + ' ' + this.selectedBrand.text;

      if (this.selectedSubBrand.text.length > 0)
        skuName = skuName + ' ' + this.selectedSubBrand.text;

      if (this.selectedFlavor.text.length > 0)
        skuName = skuName + ' ' + this.selectedFlavor.text;

      if (this.selectedColor.text.length > 0)
        skuName = skuName + ' ' + this.selectedColor.text;

      if (this.selectedABV.text.length > 0)
        skuName = skuName + ' ' + this.selectedABV.text;

      if (this.$refs.weightInput.value.length > 0)
        skuName = skuName + ' ' + this.$refs.weightInput.value;

      if (this.selectedQuantity.text.length > 0)
        skuName = skuName + ' ' + this.selectedQuantity.text;

      if (this.selectedPack.text.length > 0)
        skuName = skuName + ' ' + this.selectedPack.text;

      if (this.selectedOrigin.text.length > 0)
        skuName = skuName + ' ' + this.selectedOrigin.text;

      if (this.selectedExtra.text.length > 0)
        skuName = skuName + ' ' + this.selectedExtra.text;

      skuName.replace('  ', ' ').trim();
      this.waitingGoodSku = skuName;

      var product = {
        id: this.selectedGoodSku.id,
        sku: skuName,
        productGroupID: this.selectedGroup.id,
        productSubGroupID: this.selectedSubGroup.id,
        productTypeID: this.selectedType.id,
        productBrandID: this.selectedBrand.id,
        productSubBrandID: this.selectedSubBrand.id,
        productFlavourID: this.selectedFlavor.id,
        productPackageID: this.selectedPack.id,
        productQuantityID: this.selectedQuantity.id,
        productManufacturerID: this.selectedManufacturer.id,
        productOriginID: this.selectedOrigin.id,
        weight: this.$refs.weightInput.value,
        mass: this.$refs.massInput.value.replace(',', '.'),
        productGoodID: this.selectedGoods.id,
        productColourID: this.selectedColor.id,
        productExtraID: this.selectedExtra.id,
        productABVID: this.selectedABV.id,
        productClientGroupID: this.selectedClientGroup.id,
        author: this.$store.getters.getAssignee,
        classId: this.selectedClass.id
      }
      sendChangeGoodProduct(product);
    },
    confirmSku() {
      this.isConfirmingSku = true;
      this.waitingRawSku = this.selectedRawSku.id;
      sendConfirmProduct({
        rawId: this.selectedRawSku.id,
        goodId: this.selectedGoodSku.id,
        goodSku: this.selectedGoodSku.sku
      });
    },
    getCounters() {
      if (this.getCounterCancelToken) {
        this.getCounterCancelToken.cancel('Cancel');
      }
      this.getCounterCancelToken = axios.CancelToken.source();
      api.getCounters(this.rawFilterInput, this.selectedClass.id, this.employeeFilterInput, this.isWithoutConfirm, this.getCounterCancelToken).then(response => {
        this.filteredRawCounter = response.data[0];
        this.rawCounter = response.data[1];
        this.getCounterCancelToken = null;
      })
    },
    addProperty(table, value, arrayList, input, selectedField) {
      api.addProperty(table, value, this.selectedClass.flag).then(response => {
        if (typeof (response.data) === 'number') {
          var property = {hover: false, id: response.data, text: value};
          arrayList.push(property);
          arrayList.sort(this.dynamicSort('Name'));
          selectedField.text = value;
          selectedField.id = response.data;
          input.value = value;
        } else alert(response.data);
      })
    },
    changeProperty(table, value, arrayList, input, selectedField) {
      api.changeProperty(table, value, this.selectedClass.flag, selectedField.id).then(response => {
        if (typeof (response.data) === 'number') {
          selectedField.text = value;
          selectedField.id = response.data;
          input.value = value;
          arrayList.sort(this.dynamicSort('Name'));
        } else alert(response.data);
      })
    },
    copyText() {
      if (this.lastClick.tagName == "TD") {
        var area = document.createElement('textarea');

        document.body.appendChild(area);
        area.value = this.lastClick.innerText;
        area.select();
        document.execCommand("copy");
        document.body.removeChild(area);
      }
    },
    click(el) {
      this.lastClick = el;
      if (el.tagName !== 'TD') {
        this.isFocusOnGoodSkuList = false;
        this.isFocusOnRawSkuList = false;
      }
    },
    upSelect(list) {
      this.isMouseMode = false;
      this.timeout = 10;
      let currentId = 0;
      for (let i = 1; i < list.length; i++) {
        if (list[i].hover) {
          list[i - 1].hover = true;
          list[i].hover = false;
          currentId = list[i - 1].id;
          break;
        }
      }
      this.moveUp(list, currentId)
    },
    downSelect(list) {
      this.isMouseMode = false;
      this.timeout = 10;
      let currentId = 0;
      for (let i = 0; i < list.length - 1; i++) {
        if (list[i].hover) {
          list[i + 1].hover = true;
          list[i].hover = false;
          currentId = list[i + 1].id
          break;
        }
      }
      this.moveDown(list, currentId, 'end')
    },
    moveUp(list, currentId) {
      let activeSelect = document.activeElement.nextSibling;
      let currentEl = activeSelect.querySelector('#id' + currentId);
      var targetPosition = {
            top: window.pageYOffset + currentEl.getBoundingClientRect().top,
            bottom: window.pageYOffset + currentEl.getBoundingClientRect().bottom
          },
          // Получаем позиции окна
          windowPosition = {
            top: window.pageYOffset + activeSelect.getBoundingClientRect().top + 20,
            bottom: window.pageYOffset + activeSelect.getBoundingClientRect().bottom
          };
      if (targetPosition.bottom < windowPosition.top || // Если позиция нижней части элемента больше позиции верхней чайти окна, то элемент виден сверху
          targetPosition.top > windowPosition.bottom) { // Если позиция верхней части элемента меньше позиции нижней чайти окна, то элемент виден снизу
        currentEl.scrollIntoView({alignToTop: "true", behavior: "auto", block: "start"});
      }
    },
    moveDown(list, currentId, block) {
      for (let i = 0; i < list.length - 1; i++) {
        if (list[i].id === currentId) {
          list[i].hover = true;
          break;
        }
      }
      let activeSelect = document.activeElement.nextSibling;
      let currentEl = activeSelect.querySelector('#id' + currentId);
      var targetPosition = {
            top: window.pageYOffset + currentEl.getBoundingClientRect().top,
            bottom: window.pageYOffset + currentEl.getBoundingClientRect().bottom
          },
          // Получаем позиции окна
          windowPosition = {
            top: window.pageYOffset + activeSelect.getBoundingClientRect().top + 20,
            bottom: window.pageYOffset + activeSelect.getBoundingClientRect().bottom - 20
          };
      if (targetPosition.bottom < windowPosition.top || // Если позиция нижней части элемента больше позиции верхней чайти окна, то элемент виден сверху
          targetPosition.top > windowPosition.bottom) { // Если позиция верхней части элемента меньше позиции нижней чайти окна, то элемент виден снизу
        currentEl.scrollIntoView({alignToTop: "true", behavior: "auto", block: block});
      }
    },
    homeSelect(list) {
      for (let i = 0; i < list.length; i++) {
        if (list[i].hover) {
          list[i].hover = false;
          break;
        }
      }
      list[0].hover = true;

      let activeSelect = document.activeElement.nextSibling;
      let currentEl = activeSelect.querySelector('#id' + list[0].id);
      currentEl.scrollIntoView({alignToTop: "true", behavior: "auto", block: "end"});
    },
    endSelect(list) {
      for (let i = 0; i < list.length; i++) {
        if (list[i].hover) {
          list[i].hover = false;
          break;
        }
      }
      list[list.length - 1].hover = true;
      let activeSelect = document.activeElement.nextSibling;
      let currentEl = activeSelect.querySelector('#id' + list[list.length - 1].id);
      currentEl.scrollIntoView({alignToTop: "true", behavior: "auto", block: "end"});
    },
    enterSelect(list, nameList) {
      if (nameList == 'skuGroupList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectGroup(list[i])
            this.$refs.groupList.value = this.selectedGroup.text;
            this.$refs.groupInput.value = this.selectedGroup.text;
            this.$refs.groupList.blur();
          }
        }
      }
      if (nameList == 'skuClientGroupList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectClientGroup(list[i])
            this.$refs.clientGroupList.value = this.selectedClientGroup.text;
            this.$refs.clientGroupInput.value = this.selectedClientGroup.text;
            this.$refs.clientGroupList.blur();
          }
        }
      }
      if (nameList == 'skuSubGroupList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectSubGroup(list[i])
            this.$refs.subGroupList.value = this.selectedSubGroup.text;
            this.$refs.subGroupInput.value = this.selectedSubGroup.text;
            this.$refs.subGroupList.blur();
          }
        }
      }
      if (nameList == 'skuTypeList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectType(list[i])
            this.$refs.typeList.value = this.selectedType.text;
            this.$refs.typeInput.value = this.selectedType.text;
            this.$refs.typeList.blur();
          }
        }
      }
      if (nameList == 'skuGoodsList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectGoods(list[i])
            this.$refs.goodsList.value = this.selectedGoods.text;
            this.$refs.goodsInput.value = this.selectedGoods.text;
            this.$refs.goodsList.blur();
          }
        }
      }
      if (nameList == 'skuBrandList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectBrand(list[i])
            this.$refs.brandList.value = this.selectedBrand.text;
            this.$refs.brandInput.value = this.selectedBrand.text;
            this.$refs.brandList.blur();
          }
        }
      }
      if (nameList == 'skuSubBrandList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectSubBrand(list[i])
            this.$refs.subBrandList.value = this.selectedSubBrand.text;
            this.$refs.subBrandInput.value = this.selectedSubBrand.text;
            this.$refs.subBrandList.blur();
          }
        }
      }
      if (nameList == 'skuColorList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectColor(list[i])
            this.$refs.colorList.value = this.selectedColor.text;
            this.$refs.colorInput.value = this.selectedColor.text;
            this.$refs.colorList.blur();
          }
        }
      }
      if (nameList == 'skuFlavorList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectFlavor(list[i])
            this.$refs.flavorList.value = this.selectedFlavor.text;
            this.$refs.flavorInput.value = this.selectedFlavor.text;
            this.$refs.flavorList.blur();
          }
        }
      }
      if (nameList == 'skuExtraList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectExtra(list[i])
            this.$refs.extraList.value = this.selectedExtra.text;
            this.$refs.extraInput.value = this.selectedExtra.text;
            this.$refs.extraList.blur();
          }
        }
      }
      if (nameList == 'skuPackList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectPack(list[i])
            this.$refs.packList.value = this.selectedPack.text;
            this.$refs.packInput.value = this.selectedPack.text;
            this.$refs.packList.blur();
          }
        }
      }
      if (nameList == 'skuQuantityList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectQuantity(list[i])
            this.$refs.quantityList.value = this.selectedQuantity.text;
            this.$refs.quantityInput.value = this.selectedQuantity.text;
            this.$refs.quantityList.blur();
          }
        }
      }
      if (nameList == 'skuManufacturerList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectManufacturer(list[i])
            this.$refs.manufacturerList.value = this.selectedManufacturer.text;
            this.$refs.manufacturerInput.value = this.selectedManufacturer.text;
            this.$refs.manufacturerList.blur();
          }
        }
      }
      if (nameList == 'skuOriginList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectOrigin(list[i])
            this.$refs.originList.value = this.selectedOrigin.text;
            this.$refs.originInput.value = this.selectedOrigin.text;
            this.$refs.originList.blur();
          }
        }
      }
      if (nameList == 'skuABVList') {
        for (let i = 0; i < list.length; i++) {
          if (list[i].hover) {
            this.selectABV(list[i])
            this.$refs.abvList.value = this.selectedABV.text;
            this.$refs.abvInput.value = this.selectedABV.text;
            this.$refs.abvList.blur();
          }
        }
      }

      //     skuABVList: '',
    },
    hoverSelect(list, element) {
      if (this.isMouseMode) {
        list.forEach(e => e.hover = false);
        element.hover = true;
      }
    },
    setMouseMode() {
      if (this.timeout === 0) {
        this.isMouseMode = true;
      } else {
        this.timeout = this.timeout - 1;
        setTimeout(this.setMouseMode, 200)
      }
    },
    loadMoreGoodProducts() {
      this.busyGood = true;
      setTimeout(() => {
        this.goodSkuSkipCount = this.goodSkuSkipCount + 1;
        this.goodSkuScrolloingCancelToken = axios.CancelToken.source();
        api.refreshGoodSKU(this.goodFilterInput, this.selectedClass.id, this.attributeFilterInput,
            this.goodSkuScrolloingCancelToken, 5000, this.goodSkuSkipCount).then(response => {
          response.data.forEach(el => {
            this.goodSkuList.push(el);
            this.busyGood = false;
          })
          this.goodSkuScrolloingCancelToken = null;
        }).catch(e => {
          this.errors.push(e);
          this.busyGood = false;
          if (!e.contains('Cancel'))
            alert(e);
        })

      }, 100);
    },
    loadMoreRawProducts() {
      this.busyRaw = true;
      setTimeout(() => {
        this.rawSkuSkipCount = this.rawSkuSkipCount + 1;
        this.rawSkuScrollingCancelToken = axios.CancelToken.source();
        api.refreshRawSKU(this.rawFilterInput, this.selectedClass.id, this.isWithoutConfirm,
            this.employeeFilterInput, this.rawSkuScrollingCancelToken, 5000, this.rawSkuSkipCount).then(response => {
          response.data.forEach(el => {
            this.rawSkuList.push(el);
            this.busyRaw = false;
          })
          this.rawSkuScrollingCancelToken = null;

        }).catch(e => {
          this.errors.push(e);
          this.busyRaw = false;
          if (!e.contains('Cancel'))
            alert(e);
        })
      }, 100);
    },
    checkWebSockets() {
      var time = 2500;
      var check = () => {
        if (!isConnectedGoodProduct() || !isConnectedChangeGoodProduct() || !isConnectedConfirmGoodProduct())
          time = 2500;
        else
          time = 100;
        if (!isConnectedGoodProduct()) {
          this.isReconnectedGoodProductWS = true;
          connectGoodProduct()
          var recGoodProduct = setInterval(() => {
            if (this.isReconnectedGoodProductWS && isConnectedGoodProduct()){
              this.isReconnectedGoodProductWS = false;
              api.getLastGoodProduct().then(response => {
                response.data.forEach(el => {
                  this.updateGoodProduct(el);
                })
              }).catch(e => {
                console.log(e);
              })
              clearInterval(recGoodProduct);
            }
          }, 100)
        }


        if (!isConnectedChangeGoodProduct()) {
          this.isReconnectedChangeGoodProductWS = true;
          connectChangeGoodProduct()
          var recChangeGoodProduct = setInterval(() => {
            if (this.isReconnectedChangeGoodProductWS && isConnectedChangeGoodProduct()){
              this.isReconnectedChangeGoodProductWS = false;
              api.getLastGoodProduct().then(response => {
                response.data.forEach(el => {
                  this.changeGoodProduct(el);
                })
              }).catch(e => {
                console.log(e);
              })
              clearInterval(recChangeGoodProduct);
            }
          }, 100)
        }


        if (!isConnectedConfirmGoodProduct()) {
          this.isReconnectedConfirmGoodProductWS = true;
          connectConfirmProduct()
          var recConfirmGoodProduct = setInterval(() => {
            if (this.isReconnectedConfirmGoodProductWS && isConnectedConfirmGoodProduct()){
              this.isReconnectedConfirmGoodProductWS = false;
              api.getLastConfirmedProduct().then(response => {
                response.data.forEach(el => {
                  this.confirmProduct(el);
                })
              }).catch(e => {
                console.log(e);
              })
              clearInterval(recConfirmGoodProduct);
            }
          }, 100)
        }
        setTimeout(check, time)
      }
      setTimeout(check, time)
    },
    confirmProduct(response){
      try {
        if (response.goodId > -1) {
          this.rawSkuList.forEach(e => {
            if (response.rawId === e.id) {
              e.goodId = response.goodId;
              e.goodSku = response.goodSku;
            }
            if (this.waitingRawSku === response.rawId) {
              this.waitingRawSku = 0
              this.selectedRawSku.goodSku = this.selectedGoodSku.sku;
              var select = false;
              var stop = false;
              this.rawSkuList.forEach(e => {
                if (select && !stop) {
                  this.selectedRawSku.hover = false;
                  this.selectedRawSku = e;
                  this.selectedRawSku.hover = true;
                  stop = true;
                }
                if (e.hover && !stop) {
                  select = true;
                }
              })
              this.$refs.filter.select();
              this.isConfirmingSku = false;
            }
          })
        } else if (this.waitingRawSku === response.rawId) {
          this.waitingRawSku = 0
          this.isConfirmingSku = false;
          alert(response.goodSku)
        }
        this.getCounters();
      } catch (e) {
        this.waitingRawSku = 0
        this.isConfirmingSku = false;
        console.log(e)
      }
    },
    changeGoodProduct(response){
      try {
        if (response.id > -1) {
          this.goodSkuList.forEach(e => {
            if (e.id === response.id) {
              e.sku = response.sku;
              if (this.selectedGoodSku.id === response.id) {
                this.selectGoodRow(this.selectedGoodSku);
              }
            }
          })
        } else if (response.sku === this.waitingGoodSku) {
          alert(response.manufacturer);
        }
      } catch (e) {
        console.log(e)
      }
    },
    updateGoodProduct(response){
      if (response.id > -1 && response.sku === this.waitingGoodSku) {
        this.waitingGoodSku = '';
        this.goodSkuList.forEach(e => {
          e.hover = false
        })
        var sku = {id: response.id, sku: response.sku, hover: true}
        console.log(response.id + ': ' + response.sku)
        this.goodSkuList.push(sku);
        this.reverseGood = !this.reverseGood;
        this.sortGoodBy(this.sortGoodKey);
        this.selectGoodRow(sku);

        this.$nextTick(function () {
          this.moveToGood(sku);
        })
        this.isAddingSku = false;
      } else if (response.sku === this.waitingGoodSku) {
        this.waitingGoodSku = '';
        alert(response.manufacturer);
        this.isAddingSku = false;
      }
    }
  },

  created() {
    connectGoodProduct();
    connectChangeGoodProduct();
    connectConfirmProduct();
  },

  mounted() {
    console.log(this.$store.getters.getUserName)
    console.log(this.$store.getters.getAssignee)
    var thisPage = this;
    this.checkWebSockets();
    this.refreshProductClass();
    this.columnResizableRawId();
    this.columnResizableRawSku();
    this.columnResizableGoodId();
    this.getCounters();
    addHandlerConfirmProduct(response => {
      this.confirmProduct(response);
    })
    addHandlerChangeGoodProduct(response => {
      this.changeGoodProduct(response);
    })
    addHandlerGoodProduct(response => {
      this.updateGoodProduct(response);
    })
    addEventListener("keydown", function (e){
      if (e.ctrlKey && e.keyCode === 67){
        thisPage.copyText();
      } else if (e.keyCode === 38){ //up
        if (thisPage.isFocusOnGoodSkuList) {
          for (let i = 1; i < thisPage.goodSkuList.length; i++) {
            if(thisPage.goodSkuList[i].hover) {
              thisPage.goodSkuList[i].hover = false;
              thisPage.goodSkuList[i - 1].hover = true;
              thisPage.selectGoodRow(thisPage.goodSkuList[i - 1]);
              thisPage.moveToGood(thisPage.goodSkuList[i - 1]);
              break;
            }
          }
        }
        if (thisPage.isFocusOnRawSkuList){
          for (let i = 1; i < thisPage.rawSkuList.length; i++) {
            if(thisPage.rawSkuList[i].hover) {
              thisPage.rawSkuList[i].hover = false;
              thisPage.rawSkuList[i - 1].hover = true;
              thisPage.moveToRaw(thisPage.rawSkuList[i - 1]);
              break;
            }
          }
        }
      } else if (e.keyCode === 40){ //down
        if (thisPage.isFocusOnGoodSkuList) {
          for (let i = 0; i < thisPage.goodSkuList.length - 1; i++) {
            if(thisPage.goodSkuList[i].hover) {
              thisPage.goodSkuList[i].hover = false;
              thisPage.goodSkuList[i + 1].hover = true;
              thisPage.selectGoodRow(thisPage.goodSkuList[i + 1]);
              thisPage.moveToGood(thisPage.goodSkuList[i + 1]);
              break;
            }
          }
        }
        if (thisPage.isFocusOnRawSkuList)
          for (let i = 0; i < thisPage.rawSkuList.length - 1; i++) {
            if(thisPage.rawSkuList[i].hover) {
              thisPage.rawSkuList[i].hover = false;
              thisPage.rawSkuList[i + 1].hover = true;
              thisPage.moveToRaw(thisPage.rawSkuList[i + 1]);
              break;
            }
          }
      }
    });
    addEventListener("keydown", function(e) {
      // space and arrow keys
      if([38, 40].indexOf(e.keyCode) > -1) {
        e.preventDefault();
      }
    });
  },
  updated(){
    this.setMouseMode();
  },
  beforeDestroy() {
    disconnectChangeGoodProduct()
    disconnectConfirmGoodProduct()
    disconnectGoodProduct()
  }
}
</script>

<style scoped>
#page {
  margin-top: 35px;
  justify-content: flex-start;
  flex-wrap: nowrap;
  display: flex;
  flex-direction: row;
  margin-bottom: 10px;
}

#filters {
  margin-left: 7px;
  display: flex;
  flex-wrap: nowrap;
}

input, select {
  outline: none;
}

.filter {
  display: block;
  margin-bottom: 5px;
  margin-left: 3px;
  width: 99%;
  height: 25px;
  border: 1px solid black;
  background-color: #FFFFFF;
  padding-left: 2px;
}

#tableGoodSku {
  align-self: flex-start;
  height: calc(100vh - 585px);
  overflow-y: scroll;
  overflow-x: hidden;
  border: 1px solid grey;
}

.buttonConfirm {
  background-color: lightgrey;
  width: 35%;
  margin-top: 5px;
  border: 1px solid black;
  font-size: 15px;
  outline: none;
  margin-left: 45px;
  margin-right: 5px;
}

.buttonConfirm:active, .buttonCreate:active, .buttonChange:active {
  transform: translateY(0.2px);
}
.buttonConfirm:hover, .buttonCreate:hover, .buttonChange:hover {
  background-color: lightblue;
}

.buttonChange:disabled {
  border: 1px solid #999999;
  background-color: #cccccc;
  color: #666666;
}

.buttonCreate:disabled {
  border: 1px solid #999999;
  background-color: #cccccc;
  color: #666666;
}

.buttonConfirm:disabled {
  border: 1px solid #999999;
  background-color: #cccccc;
  color: #666666;
}

.buttonChange {
  background-color: lightgrey;
  width: 25%;
  margin-top: 5px;
  border: 1px solid black;
  font-size: 15px;
  outline: none;
  margin-left: 19%;
  margin-right: 5px;
}

.buttonCreate {
  background-color: lightgrey;
  width: 25%;
  margin-top: 5px;
  border: 1px solid black;
  font-size: 15px;
  outline: none;
  margin-left: 3px;
  margin-right: 5px;
}

.resizable {
  width: 10px;
  height: 22px;
  cursor: e-resize;
  border-style: none;
  margin-left: auto;
  margin-right: 0;
}
.columnName {
  display: flex;
  flex-wrap: nowrap;
}

#pageGoodSku {
  margin-left: 10px;
  width: 49%;
}

#goodFilter {
  height: 25px;
  margin-left: 10px;
  width: 39%;
}

#rawFilter {
  height: 25px;
  width: 37%;
  align-self: flex-start;
}

#confirmSkuCheckbox{
  display: flex;
  flex-wrap: nowrap;
  align-self: center;
  font-size: 12px;
  width: 12%;
  margin-left: 3px;
}

#textBlock{
  font-size: 12px;
  width: 12%;
  margin-left: 3px;
  height: 30px;
}

#filteredRawCounter{
  display: flex;
  flex-wrap: nowrap;
  align-self: center;
  font-size: 12px;
  margin-left: 3px;
}

#productGroupSelect{
  height: 25px;
  width: 100%;
  padding-left: 2px;
  margin-left: 3px;
  background-color: #e2e8ff;
  align-self: flex-start;
  border: 1px solid black;
}

#pageRawSku {
  width: 49%;
  height: calc(100vh - 110px);
  margin-left: 10px;
  align-self: flex-start;
  overflow-y: scroll;
  overflow-x: hidden;
  border: 1px solid grey;
}

#pageRawSku thead th {
  position: sticky;
  top: 0;
  border: 1px solid black;
  background-color: lightgrey;
  z-index: 1;
}

#pageGoodSku thead th {
  position: sticky;
  top: 0;
  border: 1px solid black;
  background-color: lightgrey;
  z-index: 1;
}

body {
  font-size:10px;
}

.table {
  font-size: 10pt;
  background-color: white;
  border: 2px solid whitesmoke;
}

.table th, .table td {
  padding: 1px;
  border: 1px solid #888;
  text-align: left;
  vertical-align: middle;
  position: relative;
  cursor: pointer;
}

.skuPropLabel {
  display: flex;
  justify-content: flex-start;
  flex-wrap: nowrap;
  text-align: left;
  font-size: 10pt;
  margin-top: 3px;
  align-items: center;
}

.skuPropLabel * {
  flex-shrink: 0;
}

.buttonsProp{
  margin-left: 2%;
}

.label {
  flex-wrap: nowrap;
  width: 70px;
  margin: 1px;
  white-space: nowrap;
}

.toolSelect{
  border: 1px solid lightgrey;
  position: absolute;
  z-index: 1;
  left: calc(53.8% + 84px);
  transform: translateY(102px);
  margin: auto;
  width: 18%;
  height: 180px;
  background-color: #FFFFFF;
}

.hoverOption{
  background-color: deepskyblue;
}

.lowerToolSelect {
  transform: translateY(-102px) !important;
}

.toolSearch{
  background-image: url('https://test.beltermo.su/images/tild3032-3461-4462-b466-633338353837__basics-08-128-min.png');
  background-size: 10%;
  background-repeat: no-repeat;
  background-position: right;
  box-sizing: border-box;
  -moz-box-sizing: border-box;
  -webkit-box-sizing: border-box;
  display: block;
  align-self: center;
  margin-left: 9%;
  width: 35%;
  height: 19px;
  padding-left: 2px;
  font-size: 13px;
  background-color: #FFFFFF;
  border: 1px solid black;
}

.mass{
  box-sizing: border-box;
  -moz-box-sizing: border-box;
  -webkit-box-sizing: border-box;
  display: block;
  margin-left: 9%;
  width: 80%;
  height: 22px;
  padding-left: 2px;
  font-size: 13px;
  background-color: #FFFFFF;
  border: 1px solid lightgrey;
}

.inputProp{
  box-sizing: border-box;
  -moz-box-sizing: border-box;
  -webkit-box-sizing: border-box;
  display: block;
  margin-left: 1%;
  width: 35%;
  height: 19px;
  padding-left: 2px;
  font-size: 13px;
  background-color: #FFFFFF;
  border: 1px solid lightgrey;
}
/* activeRaw */
.table .activeRaw td:after {
  content: '';
  position: absolute;
  top: 0px;
  right: 0px;
  bottom: 0px;
  left: 0px;
  border-top: 1px solid black;
  border-bottom: 1px solid black;
}
.table .activeRaw td {
  background-color: deepskyblue;
}
.table .activeRaw td:first-child:after {
  border-left: 1px solid black;
}
.table .activeRaw td:last-child:after {
  border-right: 1px solid black;
}

/* activeGood */
.table .activeGood td:after {
  content: '';
  position: absolute;
  top: 0px;
  right: 0px;
  bottom: 0px;
  left: 0px;
  border-top: 1px solid black;
  border-bottom: 1px solid black;
}
.table .activeGood td {
  background-color: deepskyblue;
}
.table .activeGood td:first-child:after {
  border-left: 1px solid black;
}
.table .activeGood td:last-child:after {
  border-right: 1px solid black;
}
</style>