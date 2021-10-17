<template>
  <v-container>
    <v-row>
      <v-col>
        <h3>Загрузка номенклатур</h3>
        <v-file-input
            chips
            multiple
            clearable
            label="Файл для загрузки"
            @change="change($event)"
            accept=".xlsx "
        />
        <v-btn :disabled="!files.length" @click="submit">Загрузить</v-btn>
      </v-col>
    </v-row>
    <v-data-table
        :headers="headers"
        :items="actionLog"
        :items-per-page="10"
        class="elevation-1"
    ></v-data-table>
  </v-container>
</template>

<script>
import api from "../product-api"
import XLSX from 'xlsx'
export default {
  name: 'HomePage',
  data: () => ({
    files: [],
    reader: null,
    jsonData: [],
    actionLog: [],
    headers: [],
    fileName: null,
    getLogInterval: null
  }),
  methods: {
    change(e) {
      this.files = e;
      console.log(e)
      console.log(e[0].name)
      this.fileName = e[0].name;
      if (this.files.length) {
        this.files.forEach(f => {
          this.reader.readAsArrayBuffer(f)
        })
      }
    },
    submit() {
      var resArr = [];
      this.jsonData.filter(function (item) {
        resArr.push(item);
      });
      resArr[0].fileName = this.fileName
      resArr[0].author = this.$store.getters.getAssignee

      console.log(resArr.length);

      api.loadSKU(resArr).then(response => {
        alert('Номенклатура загружена!');
      })
          .catch(e => {
            console.log(e)
          })
    },
    getLog(){
      api.getActionLog().then(response =>{
        this.actionLog = response.data;
        setTimeout(this.getLog, 1000)
      }).catch(e => {
        console.log(e);
        setTimeout(this.getLog, 1000)
      })
    }
  },
  // this.name = name;
  // this.author = author;
  // this.fileName = fileName;
  //private Date dateInsert;

  mounted() {
    var thisPage = this;
    this.getLogInterval = setTimeout( thisPage.getLog,1000)

    let column = {text: 'ID', value: 'id'};
    this.headers.push(column)

    column = {text: 'Описание', value: 'name'};
    this.headers.push(column)

    column = {text: 'Автор', value: 'author'};
    this.headers.push(column)

    column = {text: 'Наименование файла', value: 'fileName'};
    this.headers.push(column)

    column = {text: 'Дата и время', value: 'dateInsert'};
    this.headers.push(column)


    this.reader = new FileReader();
    this.reader.onload = (e) => {
      this.jsonData.splice(0, this.jsonData.length);
      const data = new Uint8Array(e.target.result);
      const wb = XLSX.read(data, {type: 'array'});
      this.jsonData.push(...XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]]));

    }
  },
  beforeDestroy(){
    clearInterval(this.getLogInterval)
  }
}
</script>
