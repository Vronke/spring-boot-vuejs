package web.analytics.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import web.analytics.domain.ActionLog;
import web.analytics.domain.sku.*;
import org.springframework.web.bind.annotation.*;
import web.analytics.domain.sku.Color;
import web.analytics.repository.ActionLogRepository;
import web.analytics.repository.UserRepository;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@RestController
@RequestMapping("/product")
public class ProductController {
    ProductController(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (!isUpdatingFromTempGoodSku){
                        isUpdatingFromTempGoodSku = true;
                        refreshGoodSku();
                    }
                    try {
                        sleep(333);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (!isUpdatingFromTempRawSku) {
                        isUpdatingFromTempRawSku = true;
                        refreshRawSku();
                    }
                    try {
                        sleep(333);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (!isLoadingNewSku) {
                        isLoadingNewSku = true;
                        if (newSkuLists.size() > 0){
                            String res = NewSku.addRawSku(newSkuLists.get(0));
                            if(res.equals("yay")){
                                actionLogRepository.save(new ActionLog("Загрузка номенклатуры", 0, newSkuLists.get(0)[0].author, newSkuLists.get(0)[0].fileName));
                            } else {
                                actionLogRepository.save(new ActionLog("Ошибка загрузки! " + res, 0, newSkuLists.get(0)[0].author, newSkuLists.get(0)[0].fileName));
                            }
                            newSkuLists.remove(0);
                        }
                        isLoadingNewSku = false;
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        thread2.start();
        thread3.start();
    }

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ActionLogRepository actionLogRepository;

    public static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    List<NewSku[]> newSkuLists = new ArrayList<>();

    private static boolean isUpdatingFromTempRawSku = false;
    private static boolean isUpdatingFromTempGoodSku = false;
    private static boolean isLoadingNewSku = false;

    private static ArrayList<RawProduct> RawProductFromDB = RawProduct.loadSku();
    private static ArrayList<GoodProduct> GoodProductFromDB = GoodProduct.loadSku();

    @GetMapping(path = "/getClassList")
    public ArrayList<ProductClass> getClassList(){
        return ProductClass.selectClassList();
    }

    @PostMapping(path = "/refreshRawSku/")
    public List<RawProduct> refreshRawSKU(@RequestBody Filter[] filter) {
        try {
            //refreshRawSku();
            String finalFilter = filter[0].text.toLowerCase();
            List<RawProduct> result = new ArrayList<>(RawProductFromDB);
            for (String f :
                    finalFilter.split("♦")) {
                if (filter[0].isWithoutConfirm){
                    result =  result.stream().filter((s)->
                            s.goodSku.equals("") &&
                                    s.classId == filter[0].flag &&
                                    s.sku.toLowerCase().contains(f) && s.assignee.toLowerCase().contains(filter[0].attribute.toLowerCase()))
                            .collect(Collectors.toList());
                }
                else{
                    result =  result.stream().filter((s)->
                            s.classId == filter[0].flag &&
                                    (s.sku.toLowerCase().contains(f) || s.goodSku.toLowerCase().contains(f))
                                    && s.assignee.toLowerCase().contains(filter[0].attribute.toLowerCase()))
                            .collect(Collectors.toList());
                }
            }



            for (RawProduct p:
                    result) {
                p.hover = false;
            }

            if (result.size()>0 && filter[0].skip == 0)
                result.get(0).hover = true;

            return result.stream().skip(filter[0].skip * 5000)
                    .limit(filter[0].count).collect(Collectors.toList());
        }
        catch (Exception e){
            LOG.info("refreshRawSKU");
            LOG.info(e.getMessage());
        }

        return null;
    }

    @PostMapping(path = "/refreshGoodSku/")
    public List<GoodProduct> refreshGoodSKU(@RequestBody Filter[] filter) {
        try {
            //refreshGoodSku();

            String finalFilter = filter[0].text.toLowerCase();
            List<GoodProduct> result = new ArrayList<>(GoodProductFromDB);

            for (String f :
                    finalFilter.split("♦")) {
                result =  result.stream().filter((s)->
                        s.classId == filter[0].flag &&
                                s.sku.toLowerCase().contains(f) &&
                                (s.manufacturer.toLowerCase().contains(filter[0].attribute.toLowerCase())
                                        || s.type.toLowerCase().contains(filter[0].attribute.toLowerCase())
                                        || s.subgroup.toLowerCase().contains(filter[0].attribute.toLowerCase())))
                        .collect(Collectors.toList());
            }


            for (GoodProduct p:
                    result) {
                p.hover = false;
            }

            if (result.size()>0 && filter[0].skip == 0)
                result.get(0).hover = true;
            return result.stream().skip(filter[0].skip * 5000)
                    .limit(filter[0].count).collect(Collectors.toList());
        }
        catch (Exception e){
            LOG.info("refreshGoodSKU");
            LOG.info(e.getMessage());
        }
        return null;
    }

    @GetMapping(path = "/getSku/{id}")
    public Product getSku(@PathVariable("id") int id){
        try {
            return new Product(id);
        }
        catch (Exception e){
            LOG.info("getSku");
            LOG.info(e.getMessage());
        }
        return null;
    }


    @PostMapping(path = "/getGroupList/")
    public ArrayList<Group> getGroupList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return Group.selectGroupList(filter[0].text, filter[0].flag);
    }
    @PostMapping(path = "/getClientGroupList/")
    public ArrayList<ClientGroup> getClientGroupList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return ClientGroup.selectClientGroupList(filter[0].text, filter[0].flag);
    }

    @PostMapping(path = "/getSubGroupList/")
    public ArrayList<SubGroup> getSubGroupList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return SubGroup.selectSubGroupList(filter[0].text, filter[0].flag);
    }

    @PostMapping(path = "/getTypeList/")
    public ArrayList<Type> getTypeList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return Type.selectTypeList(filter[0].text, filter[0].flag);
    }
    @PostMapping(path = "/getGoodsList/")
    public ArrayList<Goods> getGoodsList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return Goods.selectGoodsList(filter[0].text, filter[0].flag);
    }

    @PostMapping(path = "/getBrandList/")
    public ArrayList<Brand> getBrandList(@RequestBody Filter[] filter){
        return Brand.selectBrandList(filter[0].text, filter[0].flag, filter[0].isWithoutConfirm);
    }

    @PostMapping(path = "/getSubBrandList/")
    public ArrayList<SubBrand> getSubBrandList(@RequestBody Filter[] filter){
        return SubBrand.selectSubBrandList(filter[0].text, filter[0].flag, filter[0].isWithoutConfirm);
    }

    @PostMapping(path = "/getColorList/")
    public ArrayList<Color> getColorList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return Color.selectColorList(filter[0].text, filter[0].flag);
    }

    @PostMapping(path = "/getFlavorList/")
    public ArrayList<Flavour> getFlavourList(@RequestBody Filter[] filter){
        return Flavour.selectFlavourList(filter[0].text, filter[0].flag, filter[0].isWithoutConfirm);
    }

    @PostMapping(path = "/getExtraList/")
    public ArrayList<Extra> getExtraList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return Extra.selectExtraList(filter[0].text, filter[0].flag);
    }

    @PostMapping(path = "/getPackageList/")
    public ArrayList<Pack> getPackList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return Pack.selectPackList(filter[0].text, filter[0].flag);
    }

    @PostMapping(path = "/getQuantityList/")
    public ArrayList<Quantity> getQuantityList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return Quantity.selectQuantityList(filter[0].text, filter[0].flag);
    }

    @PostMapping(path = "/getManufacturerList/")
    public ArrayList<Manufacturer> getManufacturerList(@RequestBody Filter[] filter){
        return Manufacturer.selectManufacturerList(filter[0].text, filter[0].flag, filter[0].isWithoutConfirm);
    }

    @PostMapping(path = "/getOriginList/")
    public ArrayList<Origin> getOriginList(@RequestBody Filter[] filter){
        if (filter[0].isWithoutConfirm)
            filter[0].text = "";
        return Origin.selectOriginList(filter[0].text, filter[0].flag);
    }

    @PostMapping(path = "/getABVList/")
    public ArrayList<ABV> getABVList(@RequestBody Filter[] filter){
        return ABV.selectABVList(filter[0].text, filter[0].flag, filter[0].isWithoutConfirm);
    }

    @MessageMapping("/createSku")
    @SendTo("/topic/createProduct")
    public GoodProduct message(GoodProduct product) {
        LOG.info("creatingSku");
        LOG.info(product.toString());
        try {
            GoodProduct result = GoodProduct.createSku(product);
            return result;
        }
        catch (Exception e){
            LOG.info("createSKU");
            LOG.info(e.getMessage());
        }
        return null;
    }

    @GetMapping(path = "/reconnectedGoodProduct")
    public ArrayList<GoodProduct> reconnectedGoodProduct(){
        return GoodProduct.getLastGoodProduct();
    }

    @GetMapping(path = "/reconnectedRawProduct")
    public ArrayList<Confirm> reconnectedRawProduct(){
        return Confirm.getLastConfirmedProduct();
    }

    @MessageMapping("updateSku")
    @SendTo("/topic/changeProduct")
    public GoodProduct updateSku(@RequestBody GoodProduct product) {
        try {
            GoodProduct result = GoodProduct.updateSku(product);
            return result;
        } catch (Exception e) {
            LOG.info("updateSKU");
            LOG.info(e.getMessage());
        }
        return null;
    }

    public void sendUpdatedSku(@RequestBody GoodProduct product) {
        this.template.convertAndSend("/topic/changeProduct", product);
    }

    @MessageMapping("confirmSku")
    @SendTo("/topic/confirmProduct")
    public Confirm confirmSku(@RequestBody Confirm confirm){
        LOG.info("ConfirmingSku");
        LOG.info(confirm.toString());
        return Confirm.confirmSku(confirm);
    }

    public void sendConfirmedSku(@RequestBody Confirm confirm){
        LOG.info("sendingConfirmedSku");
        this.template.convertAndSend("/topic/confirmProduct", confirm);
    }

    @PostMapping(path = "/addProperty")
    public String addProperty(@RequestBody Property[] property){
        return Property.addProperty(property[0]);
    }

    @PostMapping(path = "/changeProperty")
    public String changeProperty(@RequestBody Property[] property){
        return Property.changeProperty(property[0]);
    }

    @PostMapping(path = "/getCounters")
    public long[] getCounters(@RequestBody Filter[] filter){
        long[] counters = new long[2];
        counters[1] = getRawCounterSku(filter[0].flag);
        if (filter[0].text.equals("") && filter[0].attribute.equals(""))
            counters[0] = counters[1];
        else
            counters[0] = getFilteredRawCounterSku(filter[0]);
        return counters;
    }

    private static long getFilteredRawCounterSku(Filter filter){
        String finalFilter = filter.text.toLowerCase();
        List<RawProduct> result = new ArrayList<>(RawProductFromDB);
        for (String f :
                finalFilter.split("♦")) {
            if (filter.isWithoutConfirm){
                result =  result.stream().filter((s)->
                        s.goodSku.equals("") &&
                                s.classId == filter.flag &&
                                s.sku.toLowerCase().contains(f) && s.assignee.toLowerCase()
                                .contains(filter.attribute.toLowerCase())).collect(Collectors.toList());
            }
            else{
                result =  result.stream().filter((s)->
                        s.classId == filter.flag &&
                                (s.sku.toLowerCase().contains(f) || s.goodSku.toLowerCase().contains(f))
                                && s.assignee.toLowerCase()
                                .contains(filter.attribute.toLowerCase())).collect(Collectors.toList());
            }
        }
        return result.size();
    }

    private static long getRawCounterSku(int flag){
        ArrayList<RawProduct> list = new ArrayList<>(RawProductFromDB);
        long counter = list.stream().filter((s)-> s.classId == flag && s.goodSku.equals("")).count();
        return counter;
    }

    @PostMapping(path = "/loadsku")
    public String loadSKU(@RequestBody NewSku[] newSku) {
        newSkuLists.add(newSku);
        return "yay";
    }

    public void refreshGoodSku(){
        try {
            ArrayList<GoodProduct> add = GoodProduct.addSkuFromTemp();
            if (add.size() > 0){
                ArrayList<GoodProduct> newGoodProducts = new ArrayList<>(GoodProductFromDB);
                for (GoodProduct p :
                        add) {
                    Iterator<GoodProduct> iterator = newGoodProducts.iterator();
                    do{
                        GoodProduct product = iterator.next();
                        if (product.getId() == p.getId()){
                            iterator.remove();
                            break;
                        }
                    } while (iterator.hasNext());
                }
                add.removeIf(s -> String.valueOf(s.getId()).equals(s.getSku()));
                newGoodProducts.addAll(add);
                Collections.sort(newGoodProducts, Comparator.comparing(GoodProduct::getSku));

                GoodProductFromDB = newGoodProducts;

                ArrayList<RawProduct> update = new ArrayList<>(RawProductFromDB);
                for (RawProduct product :
                        update) {
                    for (GoodProduct gp :
                            add) {
                        if (product.goodId == gp.id) {
                            product.goodSku = gp.sku;
                        }
                    }
                }
                for (GoodProduct gp :
                        add) {
                    sendUpdatedSku(gp);
                }
                RawProductFromDB = update;
            }
        }
        catch (Exception e){
            LOG.info("refreshGoodSKUMethod");
            LOG.info(e.getMessage());
        }
        finally {
            isUpdatingFromTempGoodSku = false;
        }
    }
    public void refreshRawSku(){
        try{
            ArrayList<RawProduct> add = RawProduct.addSkuFromTemp();
            if (add.size() > 0){
                ArrayList<RawProduct> newRawProducts = new ArrayList<>(RawProductFromDB);
                for (RawProduct p :
                        add) {
                    Iterator<RawProduct> iterator = newRawProducts.iterator();
                    do {
                        RawProduct product = iterator.next();
                        if (product.getId() == p.getId()){
                            iterator.remove();
                            break;
                        }
                    } while (iterator.hasNext());
                    sendConfirmedSku(p.toConfirm());
                }
                add.removeIf(s -> String.valueOf(s.getId()).equals(s.getSku()));
                newRawProducts.addAll(add);
                newRawProducts.sort(Comparator.comparing(RawProduct::getSku));
                RawProductFromDB = newRawProducts;
            }
        }
        catch (Exception e){
            LOG.info("refreshRawSKUMethod");
            LOG.info(e.getMessage());
        }
        finally {
            isUpdatingFromTempRawSku = false;
        }
    }

    @GetMapping(path = "/getActionLog")
    public List<ActionLog> getActionLog(){
        List<ActionLog> actionLog = actionLogRepository.findByTypeId(0);
        actionLog.sort(Comparator.comparing(ActionLog::getIdDesc));
        return actionLog;
    }


}
