package web.analytics.controller;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import web.analytics.domain.Authority;
import web.analytics.domain.Fias;
import web.analytics.domain.RawAddress;
import web.analytics.domain.User;
import web.analytics.repository.AuthorityRepository;
import web.analytics.repository.FiasRepository;
import web.analytics.repository.RawAddressRepository;
import web.analytics.repository.UserRepository;
import web.analytics.domain.sku.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@RestController()
@RequestMapping("/api")
public class BackendController {

    private static final Logger LOG = LoggerFactory.getLogger(BackendController.class);

    private String uploadPath = "C:\\Users\\s.petranin\\Desktop\\test\\";

    public static final String HELLO_TEXT = "Hello from Spring Boot Backend!";
    public static final String SECURED_TEXT = "Hello from the secured resource!";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private RawAddressRepository rawAddressRepository;

    @Autowired
    private FiasRepository fiasRepository;

    @GetMapping(path = "/hello")
    public String sayHello() {
        LOG.info("GET called on /hello resource");
        Product product = new Product();
        return HELLO_TEXT;
    }

    @PostMapping(path = "/registration")
    public String addNewUser (@RequestParam("password") String password, @RequestParam("username") String username,
                              @RequestParam("assignee") String assignee) {

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordHash = encoder.encode(password);

        User savedUser = userRepository.save(new User(username, passwordHash, assignee));
        authorityRepository.save(new Authority(username, "U"));

        LOG.info(savedUser.toString() + " successfully saved into DB");

        return savedUser.getId().toString();
    }


    @PostMapping(path = "/loadaddresses")
    public String LoadAddresses(@RequestBody String json){

//        List<RawAddress> uniqueAddresses = new ArrayList<>();
//        for (int i = 0; i < rawAddresses.length; i++) {
//            int finalI = i;
//            if(uniqueAddresses.stream().noneMatch(o -> o.getInn().equals(rawAddresses[finalI].getInn()) && o.getRawAddress().equals(rawAddresses[finalI].getRawAddress())))
//                uniqueAddresses.add(new RawAddress(rawAddresses[i].getInn(), rawAddresses[i].getRawAddress()));
//        }

        LOG.info(json);
//        List<RawAddress> addresses = new ArrayList<>();
//        for (RawAddress r:
//                rawAddresses) {
//            if(rawAddressRepository.findByRawAddressAndInn(r.getRawAddress(), r.getInn()).size() == 0)
//                addresses.add(new RawAddress(r.getInn(), r.getRawAddress()));
//        }
//
//        rawAddressRepository.saveAll(addresses);
//        LOG.info(addresses.size() + " was added to database");
//        return addresses.size() + " was added to database";
        return json;
    }

    @GetMapping(path="/main")
    public @ResponseBody String getSecured() {

        return SECURED_TEXT;
    }

    @GetMapping(path = "/authority/{username}")
    public String getAuthority(@PathVariable("username") String username){
        return authorityRepository.findByUsername(username).get(0).getAuthority();
    }
    @GetMapping(path = "/assignee/{username}")
    public String getAssignee(@PathVariable("username") String username){
        return userRepository.findByUsername(username).get(0).getAssignee();
    }

    @PostMapping(path = "/service")
    public String addFile(@RequestParam("file") MultipartFile file, @RequestParam("name") String name)
        throws IOException {


        if (file != null){
            File uploadDir = new File(uploadPath + name);
            String resultFilename = uploadDir + "\\" + file.getOriginalFilename();

            if (!uploadDir.exists()){
                uploadDir.mkdir();
            }

            File resultFile = new File(resultFilename);
            file.transferTo(resultFile);
            return fiasLoader(resultFile);
        }
        return "NOT YAY";
    }

    public String fiasLoader(File file) {
        //инициализируем потоки
        int rows = 0;
        List<Fias> fiasList = new ArrayList<>();
        Workbook workBook = null;
//        XSSFWorkbook workBook = null;

        try {

            workBook = WorkbookFactory.create(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //разбираем первый лист входного файла на объектную модель
        Sheet sheet = workBook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        //проходим по всему листу
        while (rowIterator.hasNext()) {
            rows++;
            if (rows%10000 == 0){
//                fiasList.clear();
//                System.gc();
                LOG.info(String.valueOf(rows));
            }

            Fias fias = new Fias();
            Row row = rowIterator.next();
            Iterator<Cell> cells = row.iterator();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                if (cell == null)
                    continue;
                //перебираем возможные типы ячеек
                switch (i) {
                    case 0:
                            fias.setAoguid(UUID.fromString(cell.getStringCellValue()));
                        break;
                    case 1:
                            fias.setFullAddress(cell.getStringCellValue());
                        break;
                    case 2:
                            fias.setRegion(cell.getStringCellValue());
                        break;
                    case 3:
                            fias.setDistrict(cell.getStringCellValue());
                        break;
                    case 4:
                            fias.setCity(cell.getStringCellValue());
                        break;
                    case 5:
                            fias.setVillage(cell.getStringCellValue());
                        break;
                    case 6:
                            fias.setStreet(cell.getStringCellValue());
                        break;
                    case 7:
                            fias.setCode(cell.getStringCellValue());
                        break;
                    case 8:
                        if (cell.getStringCellValue().equals("1"))
                            fias.setRelevance(true);
                        else
                            fias.setRelevance(false);
                        break;
                    case 9:
                            fias.setActualAOID(UUID.fromString(cell.getStringCellValue()));
                        break;
                    case 10:
                            fias.setAoid(UUID.fromString(cell.getStringCellValue()));
                        break;
                    default:
                        break;
                }
            }
            fiasList.add(fias);
        }
        fiasRepository.saveAll(fiasList);
        return "Yay";
    }
}
