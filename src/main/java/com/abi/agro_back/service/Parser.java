package com.abi.agro_back.service;

import com.abi.agro_back.Region;
import com.abi.agro_back.collection.Agrarian;
import com.abi.agro_back.collection.Photo;
import com.abi.agro_back.collection.SellType;
import com.abi.agro_back.collection.VillageCouncil;
import com.abi.agro_back.config.StorageService;
import com.abi.agro_back.repository.AgrarianRepository;
import com.abi.agro_back.repository.VillageCouncilRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Parser {
    @Autowired
    StorageService storageService;
    @Autowired
    AgrarianRepository agrarianRepository;
    @Autowired
    VillageCouncilRepository villageCouncilRepository;
    //	List<Agrarian> agrarians = new ArrayList<>();
//	List<VillageCouncil> villageCouncils = new ArrayList<>();
    HashMap<String, String> oblToOurOblHashMap = new HashMap<>();
    HashMap<String, String> ourOblToOblHashMap = new HashMap<>();
    HashMap<String, HashMap<String, String>> regions = new HashMap<>();
    Map<String, List<Region>> ourOblRegHashmap = new HashMap<>();

    public void jsonToHashMap() throws JsonProcessingException {
//        String json1 = "{\"vinnycya\":[{\"slug\":\"vinnitsa\",\"rus\":\"Винницкий район\"},{\"slug\":\"illinetsky\",\"rus\":\"Ильинецкий район\"},{\"slug\":\"lypovetsky\",\"rus\":\"Липовецкий район\"},{\"slug\":\"litinsky\",\"rus\":\"Литинский район\"},{\"slug\":\"nemirovsky\",\"rus\":\"Немировский район\"},{\"slug\":\"orativ\",\"rus\":\"Оратовский район\"},{\"slug\":\"pohrebishche\",\"rus\":\"Погребищенский район\"},{\"slug\":\"tyvriv\",\"rus\":\"Тывровский район\"},{\"slug\":\"gaissin\",\"rus\":\"Гайсинский район\"},{\"slug\":\"bershad\",\"rus\":\"Бершадский район\"},{\"slug\":\"teplichi\",\"rus\":\"Теплицкий район\"},{\"slug\":\"trostyanets\",\"rus\":\"Тростянецкий район\"},{\"slug\":\"chechelnytsky\",\"rus\":\"Чечельницкий район\"},{\"slug\":\"zhmerynka\",\"rus\":\"Жмеринский район\"},{\"slug\":\"barsky\",\"rus\":\"Барский район\"},{\"slug\":\"shargorod\",\"rus\":\"Шаргородский район\"},{\"slug\":\"mohyliv-podilsky\",\"rus\":\"Могилев-Подольский район\"},{\"slug\":\"murovankurilovetsky\",\"rus\":\"Мурованокуриловецкий район\"},{\"slug\":\"chernive\",\"rus\":\"Черновицкий район\"},{\"slug\":\"yampil\",\"rus\":\"Ямпольский район\"},{\"slug\":\"tulchyn\",\"rus\":\"Тульчинский район\"},{\"slug\":\"kryzhopilsky\",\"rus\":\"Крыжопольский район\"},{\"slug\":\"pishchansky\",\"rus\":\"Песчанский район\"},{\"slug\":\"tomashpilsky\",\"rus\":\"Томашпольский район\"},{\"slug\":\"khmilnytsky\",\"rus\":\"Хмельницкий район\"},{\"slug\":\"kalynivsky\",\"rus\":\"Калиновский район\"},{\"slug\":\"kozyatyn\",\"rus\":\"Казатинский район\"}],\"volyn\":[{\"slug\":\"volodymyr-volynsky\",\"rus\":\"Владимир-Волынский район\"},{\"slug\":\"ivanychivsky\",\"rus\":\"Иваничевский район\"},{\"slug\":\"lokachynsky\",\"rus\":\"Локачинский район\"},{\"slug\":\"novovolynsky\",\"rus\":\"Нововолынский район\"},{\"slug\":\"ustiluh\",\"rus\":\"Устилуг\"},{\"slug\":\"kamin-kashyrsky\",\"rus\":\"Камень-Каширский район\"},{\"slug\":\"liubeshivsky\",\"rus\":\"Любешевский район\"},{\"slug\":\"manevytsky\",\"rus\":\"Маневичский район\"},{\"slug\":\"kovel\",\"rus\":\"Ковельский район\"},{\"slug\":\"liubomlsky\",\"rus\":\"Любомльский район\"},{\"slug\":\"ratnivsky\",\"rus\":\"Ратновский район\"},{\"slug\":\"starovyzhivsky\",\"rus\":\"Старовыжевский район\"},{\"slug\":\"turiysky\",\"rus\":\"Турийский район\"},{\"slug\":\"shatsky\",\"rus\":\"Шацкий район\"},{\"slug\":\"lutsk\",\"rus\":\"Луцкий район\"},{\"slug\":\"berestechko\",\"rus\":\"Берестечко\"},{\"slug\":\"horokhivsky\",\"rus\":\"Гороховский район\"},{\"slug\":\"kivertsivsky\",\"rus\":\"Киверцовский район\"},{\"slug\":\"rozhysche\",\"rus\":\"Рожищенский район\"},{\"slug\":\"olyka\",\"rus\":\"Олыка\"},{\"slug\":\"torchyn\",\"rus\":\"Торчин\"},{\"slug\":\"tsuman\",\"rus\":\"Цумань\"}],\"dnipro\":[{\"slug\":\"dniprovsky\",\"rus\":\"Днепровский район\"},{\"slug\":\"novopokrovske\",\"rus\":\"Новопокровское\"},{\"slug\":\"obukhivka\",\"rus\":\"Обуховка\"},{\"slug\":\"slobozhanske\",\"rus\":\"Слобожанское\"},{\"slug\":\"pidhorodnenske\",\"rus\":\"Подгородненское\"},{\"slug\":\"petrikivsky\",\"rus\":\"Петриковский район\"},{\"slug\":\"tsarychansky\",\"rus\":\"Царичанский район\"},{\"slug\":\"kamiansky\",\"rus\":\"Каменский район\"},{\"slug\":\"verkhnedniprovsky\",\"rus\":\"Верхнеднепровский район\"},{\"slug\":\"krynychansky\",\"rus\":\"Криничанский район\"},{\"slug\":\"piatykhatky\",\"rus\":\"Пятихатский район\"},{\"slug\":\"zhovti-vody\",\"rus\":\"Желтые Воды\"},{\"slug\":\"krivorizky\",\"rus\":\"Криворожский район\"},{\"slug\":\"apostolivsky\",\"rus\":\"Апостоловский район\"},{\"slug\":\"sofiyivsky\",\"rus\":\"Софиевский район\"},{\"slug\":\"shyrokivsky\",\"rus\":\"Широковский район\"},{\"slug\":\"nikopolsky\",\"rus\":\"Никопольский район\"},{\"slug\":\"marganets\",\"rus\":\"Марганец\"},{\"slug\":\"pokrovsk\",\"rus\":\"Покровск\"},{\"slug\":\"tomakivsky\",\"rus\":\"Томаковский район\"},{\"slug\":\"novomoskovsky\",\"rus\":\"Новомосковский район\"},{\"slug\":\"magdalinivsky\",\"rus\":\"Магдалиновский район\"},{\"slug\":\"pereshchepyne\",\"rus\":\"Перещепино\"},{\"slug\":\"pavlogradsky\",\"rus\":\"Павлоградский район\"},{\"slug\":\"ternivka\",\"rus\":\"Терновка\"},{\"slug\":\"yuriivsky\",\"rus\":\"Юрьевский район\"},{\"slug\":\"sinyelnikivsky\",\"rus\":\"Синельниковский район\"},{\"slug\":\"vasylkivsky\",\"rus\":\"Васильковский район\"},{\"slug\":\"pershotravensk\",\"rus\":\"Першотравенск\"},{\"slug\":\"petropavlivsky\",\"rus\":\"Петропавловский район\"},{\"slug\":\"pokrovsky\",\"rus\":\"Покровский район\"}],\"donetsk\":[{\"slug\":\"bakhmutske\",\"rus\":\"Бахмутский район\"},{\"slug\":\"volnovaske\",\"rus\":\"Волновахский район\"},{\"slug\":\"velikonovosilkivske\",\"rus\":\"Великоновоселковский район\"},{\"slug\":\"horlivske\",\"rus\":\"Горловский район\"},{\"slug\":\"shakhtarske\",\"rus\":\"Шахтерский район\"},{\"slug\":\"debaltsieve\",\"rus\":\"Дебальцево\"},{\"slug\":\"yenakiieve\",\"rus\":\"Енакиево\"},{\"slug\":\"snizhne\",\"rus\":\"Снежное\"},{\"slug\":\"donetske\",\"rus\":\"Донецкий район\"},{\"slug\":\"amvrosiivske\",\"rus\":\"Амвросиевский район\"},{\"slug\":\"ilovaisk\",\"rus\":\"Иловайск\"},{\"slug\":\"makiiivka\",\"rus\":\"Макеевка\"},{\"slug\":\"khartsyzk\",\"rus\":\"Харцызск\"},{\"slug\":\"yasinuvatske\",\"rus\":\"Ясиноватский район\"},{\"slug\":\"kalmiuske\",\"rus\":\"Кальмиусский район\"},{\"slug\":\"boykivske\",\"rus\":\"Бойковский район\"},{\"slug\":\"novoazovske\",\"rus\":\"Новоазовский район\"},{\"slug\":\"starobeshivske\",\"rus\":\"Старобешевский район\"},{\"slug\":\"dokuchayivske\",\"rus\":\"Докучаевск\"},{\"slug\":\"kramatorske\",\"rus\":\"Краматорский район\"},{\"slug\":\"druzhkivka\",\"rus\":\"Дружковка\"},{\"slug\":\"kostyantynivske\",\"rus\":\"Константиновский район\"},{\"slug\":\"lymanske\",\"rus\":\"Лиманский район\"},{\"slug\":\"oleksandrivske\",\"rus\":\"Александровский район\"},{\"slug\":\"slovyanske\",\"rus\":\"Славянский район\"},{\"slug\":\"mariupolske\",\"rus\":\"Мариупольский\"},{\"slug\":\"manguske\",\"rus\":\"Мангушский район\"},{\"slug\":\"nikolske\",\"rus\":\"Никольский район\"},{\"slug\":\"pokrovske\",\"rus\":\"Покровский район\"},{\"slug\":\"avdiivka\",\"rus\":\"Авдеевка\"},{\"slug\":\"dobropilsky\",\"rus\":\"Добропольский район\"},{\"slug\":\"kurakhove\",\"rus\":\"Курахово\"},{\"slug\":\"maryinka\",\"rus\":\"Марьинский район\"}],\"zhytomyr\":[{\"slug\":\"berdychivsky\",\"rus\":\"Бердичевский район\"},{\"slug\":\"andrushivsky\",\"rus\":\"Андрушёвский район\"},{\"slug\":\"ruzhynsky\",\"rus\":\"Ружинский район\"},{\"slug\":\"zhytomyrsky\",\"rus\":\"Житомирский район\"},{\"slug\":\"brusylivsky\",\"rus\":\"Брусиловский район\"},{\"slug\":\"korostyshivsky\",\"rus\":\"Коростышевский район\"},{\"slug\":\"liubarsky\",\"rus\":\"Любарский район\"},{\"slug\":\"popilniansky\",\"rus\":\"Попельнянский район\"},{\"slug\":\"pulynsky\",\"rus\":\"Пулинский район\"},{\"slug\":\"radomyshlsky\",\"rus\":\"Радомышльский район\"},{\"slug\":\"romanivsky\",\"rus\":\"Романовский район\"},{\"slug\":\"khoroshivsky\",\"rus\":\"Хорошевский район\"},{\"slug\":\"chernyakhivsky\",\"rus\":\"Черняховский район\"},{\"slug\":\"chudnivsky\",\"rus\":\"Чудновский район\"},{\"slug\":\"korostensky\",\"rus\":\"Коростенский район\"},{\"slug\":\"luhynsky\",\"rus\":\"Лугинский район\"},{\"slug\":\"malynsky\",\"rus\":\"Малинский район\"},{\"slug\":\"narodytsky\",\"rus\":\"Народицкий район\"},{\"slug\":\"ovrutsky\",\"rus\":\"Овручский район\"},{\"slug\":\"olevsky\",\"rus\":\"Олевский район\"},{\"slug\":\"novohrad-volynsky\",\"rus\":\"Новоград-Волынский район\"},{\"slug\":\"baranivsky\",\"rus\":\"Барановский район\"},{\"slug\":\"yemilchynsky\",\"rus\":\"Емильчинский район\"},{\"slug\":\"horodnytsya\",\"rus\":\"Городница\"},{\"slug\":\"dovbyske\",\"rus\":\"Довбыш\"}],\"zakarpattya\":[{\"slug\":\"berehivsky\",\"rus\":\"Береговский район\"},{\"slug\":\"vinohradivsky\",\"rus\":\"Виноградовский район\"},{\"slug\":\"mukachivsky\",\"rus\":\"Мукачевский район\"},{\"slug\":\"volovetsky\",\"rus\":\"Воловецкий район\"},{\"slug\":\"svaliavsky\",\"rus\":\"Свалявский район\"},{\"slug\":\"rakhivsky\",\"rus\":\"Раховский район\"},{\"slug\":\"tyachivsky\",\"rus\":\"Тячевский район\"},{\"slug\":\"uzhhorodsky\",\"rus\":\"Ужгородский район\"},{\"slug\":\"velykoberizniansky\",\"rus\":\"Великоберезнянский район\"},{\"slug\":\"perechynsky\",\"rus\":\"Перечинский район\"},{\"slug\":\"chop\",\"rus\":\"Чоп\"},{\"slug\":\"khustsky\",\"rus\":\"Хустский район\"},{\"slug\":\"irshavsky\",\"rus\":\"Иршавский район\"},{\"slug\":\"mizhhirsky\",\"rus\":\"Межгорский район\"}],\"zaporizhzhya\":[{\"slug\":\"berdiansky\",\"rus\":\"Бердянский район\"},{\"slug\":\"prymorsky\",\"rus\":\"Приморский район\"},{\"slug\":\"chernihivsky\",\"rus\":\"Черниговский район\"},{\"slug\":\"vasylivsky\",\"rus\":\"Васильевский район\"},{\"slug\":\"enerhodar\",\"rus\":\"Энергодар\"},{\"slug\":\"kamiansko-dniprovsky\",\"rus\":\"Каменско-Днепровский район\"},{\"slug\":\"mykhailivsky\",\"rus\":\"Михайловский район\"},{\"slug\":\"zaporizky\",\"rus\":\"Запорожский район\"},{\"slug\":\"vilniansky\",\"rus\":\"Вольнянский район\"},{\"slug\":\"novomykolaivsky\",\"rus\":\"Новониколаевский район\"},{\"slug\":\"melitopolsky\",\"rus\":\"Мелитопольский район\"},{\"slug\":\"veselivsky\",\"rus\":\"Весёловский район\"},{\"slug\":\"priazovsky\",\"rus\":\"Приазовский район\"},{\"slug\":\"iakymivsky\",\"rus\":\"Якимовский район\"},{\"slug\":\"pologivsky\",\"rus\":\"Пологовский район\"},{\"slug\":\"bilomatsky\",\"rus\":\"Бильмакский район\"},{\"slug\":\"huliaypilsky\",\"rus\":\"Гуляйпольский район\"},{\"slug\":\"orikhivsky\",\"rus\":\"Ореховский район\"},{\"slug\":\"rozivsky\",\"rus\":\"Розовский район\"},{\"slug\":\"tokmatsky\",\"rus\":\"Токмакский район\"}],\"ivano-frankivsk\":[{\"slug\":\"verkhovynsky\",\"rus\":\"Верховинский район\"},{\"slug\":\"ivano-frankivsky\",\"rus\":\"Ивано-Франковский район\"},{\"slug\":\"bohorodchansky\",\"rus\":\"Богородчанский район\"},{\"slug\":\"halytsky\",\"rus\":\"Галичский район\"},{\"slug\":\"rohatynsky\",\"rus\":\"Рогатинский район\"},{\"slug\":\"tysmenytsky\",\"rus\":\"Тысменицкий район\"},{\"slug\":\"tlumatsky\",\"rus\":\"Тлумачский район\"},{\"slug\":\"kalusky\",\"rus\":\"Калушский район\"},{\"slug\":\"dolynsky\",\"rus\":\"Долинский район\"},{\"slug\":\"rozhnyativsky\",\"rus\":\"Рожнятовский район\"},{\"slug\":\"kolomyisky\",\"rus\":\"Коломыйский район\"},{\"slug\":\"horodenkivsky\",\"rus\":\"Городенковский район\"},{\"slug\":\"sniatynsky\",\"rus\":\"Снятынский район\"},{\"slug\":\"kosivsky\",\"rus\":\"Косовский район\"},{\"slug\":\"nadvirnyansky\",\"rus\":\"Надворнянский район\"},{\"slug\":\"vorokhta\",\"rus\":\"Ворохта\"},{\"slug\":\"delyatyn\",\"rus\":\"Делятин\"},{\"slug\":\"yaremche\",\"rus\":\"Яремче\"}],\"kyiv\":[{\"slug\":\"bilotserkivsky\",\"rus\":\"Белоцерковский район\"},{\"slug\":\"volodarsky\",\"rus\":\"Володарский район\"},{\"slug\":\"rokytnyansky\",\"rus\":\"Ракитнянский район\"},{\"slug\":\"skvyrsky\",\"rus\":\"Сквирский район\"},{\"slug\":\"stavishchensky\",\"rus\":\"Ставищенский район\"},{\"slug\":\"tarashchansky\",\"rus\":\"Таращанский район\"},{\"slug\":\"boryspilsky\",\"rus\":\"Бориспольский район\"},{\"slug\":\"peryaslavsky\",\"rus\":\"Переяславский район\"},{\"slug\":\"yahotynsky\",\"rus\":\"Яготинский район\"},{\"slug\":\"brovarsky\",\"rus\":\"Броварский район\"},{\"slug\":\"baryshivsky\",\"rus\":\"Барышевский район\"},{\"slug\":\"zghurivsky\",\"rus\":\"Згуровский район\"},{\"slug\":\"buchansky\",\"rus\":\"Бучанский район\"},{\"slug\":\"borodyansky\",\"rus\":\"Бородянский район\"},{\"slug\":\"vyshneve\",\"rus\":\"Вышневое\"},{\"slug\":\"irpinsky\",\"rus\":\"Ирпенский район\"},{\"slug\":\"makarivsky\",\"rus\":\"Макаровский район\"},{\"slug\":\"vyshhorodsky\",\"rus\":\"Вышгородский район\"},{\"slug\":\"ivankivsky\",\"rus\":\"Иванковский район\"},{\"slug\":\"polissky\",\"rus\":\"Полесский район\"},{\"slug\":\"obukhivsky\",\"rus\":\"Обуховский район\"},{\"slug\":\"bohuslavsky\",\"rus\":\"Богуславский район\"},{\"slug\":\"vasylkivsky\",\"rus\":\"Васильковский район\"},{\"slug\":\"kaharlytsky\",\"rus\":\"Кагарлыкский район\"},{\"slug\":\"myronivsky\",\"rus\":\"Мироновский район\"},{\"slug\":\"rzhyshchivsky\",\"rus\":\"Ржищевский район\"},{\"slug\":\"fastivsky\",\"rus\":\"Фастовский район\"},{\"slug\":\"boiarka\",\"rus\":\"Боярка\"},{\"slug\":\"kalynivsky\",\"rus\":\"Калиновка\"},{\"slug\":\"hlevakha\",\"rus\":\"Глеваха\"}],\"kropyvnytskyi\":[{\"slug\":\"holovanivsky\",\"rus\":\"Голованевский район\"},{\"slug\":\"blahovishchensky\",\"rus\":\"Благовещенский район\"},{\"slug\":\"vilshansky\",\"rus\":\"Ольшанский район\"},{\"slug\":\"haivoronsky\",\"rus\":\"Гайворонский район\"},{\"slug\":\"novoarkhanhelsky\",\"rus\":\"Новоархангельский район\"},{\"slug\":\"kropyvnytskyi\",\"rus\":\"Кропивницкий район\"},{\"slug\":\"bobrynetsky\",\"rus\":\"Бобринецкий район\"},{\"slug\":\"dolynsky\",\"rus\":\"Долинский район\"},{\"slug\":\"znamiansky\",\"rus\":\"Знаменский район\"},{\"slug\":\"kompaniivsky\",\"rus\":\"Компанеевский район\"},{\"slug\":\"novhorodkivsky\",\"rus\":\"Новгородковский район\"},{\"slug\":\"oleksandrivskyi\",\"rus\":\"Александрийский район\"},{\"slug\":\"novoukrainsky\",\"rus\":\"Новоукраинский район\"},{\"slug\":\"dobrovelychkivsky\",\"rus\":\"Добровеличковский район\"},{\"slug\":\"malovyskivsky\",\"rus\":\"Маловисковский район\"},{\"slug\":\"novomyrhorodsky\",\"rus\":\"Новомиргородский район\"},{\"slug\":\"oleksandriia\",\"rus\":\"Александрия\"},{\"slug\":\"onufriivsky\",\"rus\":\"Онуфриевский район\"},{\"slug\":\"petrivsky\",\"rus\":\"Петровский район\"},{\"slug\":\"svitlovodsky\",\"rus\":\"Светловодский район\"}],\"lugansk\":[{\"slug\":\"alchevskyi\",\"rus\":\"Алчевский район\"},{\"slug\":\"kadiyivka\",\"rus\":\"Кадиевка\"},{\"slug\":\"zimohirivskyi\",\"rus\":\"Зимогорьевский\"},{\"slug\":\"dovzhanskyi\",\"rus\":\"Довжанский район\"},{\"slug\":\"luhanskyi\",\"rus\":\"Луганский район\"},{\"slug\":\"lutuhine\",\"rus\":\"Лутугинский район\"},{\"slug\":\"molodohvardiisk\",\"rus\":\"Молодогвардейск\"},{\"slug\":\"rovenkivskyi\",\"rus\":\"Ровеньковский район\"},{\"slug\":\"antratsyt\",\"rus\":\"Антрацитовский район\"},{\"slug\":\"rovenky\",\"rus\":\"Ровеньки\"},{\"slug\":\"khrystalne\",\"rus\":\"Хрустальный\"},{\"slug\":\"svativskyi\",\"rus\":\"Сватовский район\"},{\"slug\":\"bilokurakynskyi\",\"rus\":\"Белокуракинский район\"},{\"slug\":\"severodonetsk\",\"rus\":\"Северодонецкий район\"},{\"slug\":\"hirske\",\"rus\":\"Горское\"},{\"slug\":\"kreminskyi\",\"rus\":\"Кременской район\"},{\"slug\":\"lysychansk\",\"rus\":\"Лисичанск\"},{\"slug\":\"popasnyanskyi\",\"rus\":\"Попаснянский район\"},{\"slug\":\"rubizhne\",\"rus\":\"Рубежное\"},{\"slug\":\"starobilskyi\",\"rus\":\"Старобельский район\"},{\"slug\":\"bilovodskyi\",\"rus\":\"Беловодский район\"},{\"slug\":\"markivskyi\",\"rus\":\"Марковский район\"},{\"slug\":\"milovskyi\",\"rus\":\"Меловской район\"},{\"slug\":\"novopskovskyi\",\"rus\":\"Новопсковский район\"},{\"slug\":\"shchastynskyi\",\"rus\":\"Щастинский район\"},{\"slug\":\"stanichno-luganskiy\",\"rus\":\"Станично-Луганский район\"},{\"slug\":\"shchastia\",\"rus\":\"Щастье\"},{\"slug\":\"novoaidar\",\"rus\":\"Новоайдарский район\"}],\"lviv\":[{\"slug\":\"drohobychskyi\",\"rus\":\"Дрогобычский район\"},{\"slug\":\"borislav\",\"rus\":\"Борислав\"},{\"slug\":\"skhidnytsia\",\"rus\":\"Схидница\"},{\"slug\":\"truskavets\",\"rus\":\"Трускавец\"},{\"slug\":\"zolochivskyi\",\"rus\":\"Золочевский район\"},{\"slug\":\"brody\",\"rus\":\"Бродовский район\"},{\"slug\":\"busk\",\"rus\":\"Бусский район\"},{\"slug\":\"pidkamin\",\"rus\":\"Подкамень\"},{\"slug\":\"pomoriany\",\"rus\":\"Поморяны\"},{\"slug\":\"lvivskyi\",\"rus\":\"Львовский район\"},{\"slug\":\"bibirka\",\"rus\":\"Бибрка\"},{\"slug\":\"horodok\",\"rus\":\"Городокский район\"},{\"slug\":\"zhovkva\",\"rus\":\"Жолковский район\"},{\"slug\":\"kamianka-buzka\",\"rus\":\"Каменка-Бугский район\"},{\"slug\":\"peremyshliany\",\"rus\":\"Перемышлянский район\"},{\"slug\":\"pustomyty\",\"rus\":\"Пустомытовский район\"},{\"slug\":\"rava-ruska\",\"rus\":\"Рава-Русская\"},{\"slug\":\"sambirskyi\",\"rus\":\"Самборский район\"},{\"slug\":\"dobromyl\",\"rus\":\"Добромиль\"},{\"slug\":\"starosambir\",\"rus\":\"Старый Самбор\"},{\"slug\":\"khyriv\",\"rus\":\"Хыров\"},{\"slug\":\"stryiskyi\",\"rus\":\"Стрыйский район\"},{\"slug\":\"zhidachiv\",\"rus\":\"Жидачовский район\"},{\"slug\":\"mykolaiv\",\"rus\":\"Николаевский район\"},{\"slug\":\"morshyn\",\"rus\":\"Моршин\"},{\"slug\":\"skole\",\"rus\":\"Сколевский район\"},{\"slug\":\"slavske\",\"rus\":\"Славское\"},{\"slug\":\"khodoriv\",\"rus\":\"Ходоров\"},{\"slug\":\"chervonohradskyi\",\"rus\":\"Червоноградский район\"},{\"slug\":\"belz\",\"rus\":\"Белз\"},{\"slug\":\"radekhiv\",\"rus\":\"Радеховский район\"},{\"slug\":\"sokal\",\"rus\":\"Сокальский район\"},{\"slug\":\"yavorivskyi\",\"rus\":\"Яворовский район\"},{\"slug\":\"mostyska\",\"rus\":\"Мостисский район\"},{\"slug\":\"novoyavorivsk\",\"rus\":\"Новояворовск\"},{\"slug\":\"sudova-vyshnia\",\"rus\":\"Судовая Вишня\"},{\"slug\":\"shehyni\",\"rus\":\"Шегини\"}],\"mykolaiv\":[{\"slug\":\"bashtanskyi\",\"rus\":\"Баштанский район\"},{\"slug\":\"berezneguvatskyi\",\"rus\":\"Березнеговатский район\"},{\"slug\":\"novobuzkyi\",\"rus\":\"Новобугский район\"},{\"slug\":\"snihurivskyi\",\"rus\":\"Снигирёвский район\"},{\"slug\":\"voznesenskyi\",\"rus\":\"Вознесенский район\"},{\"slug\":\"bratskyi\",\"rus\":\"Братский район\"},{\"slug\":\"veselynivskyi\",\"rus\":\"Веселиновский район\"},{\"slug\":\"domanivskyi\",\"rus\":\"Доманёвский район\"},{\"slug\":\"yelanetskyi\",\"rus\":\"Еланецкий район\"},{\"slug\":\"yuzhnyoukrainsk\",\"rus\":\"Южноукраинск\"},{\"slug\":\"mykolaivskyi\",\"rus\":\"Николаевский район\"},{\"slug\":\"berezhanskyi\",\"rus\":\"Березанский район\"},{\"slug\":\"novoodeskyi\",\"rus\":\"Новоодесский район\"},{\"slug\":\"ochakivskyi\",\"rus\":\"Очаковский район\"},{\"slug\":\"perwomayskyi\",\"rus\":\"Первомайский район\"},{\"slug\":\"arbuzynskyi\",\"rus\":\"Арбузинский район\"},{\"slug\":\"vradiyivskyi\",\"rus\":\"Врадиевский район\"},{\"slug\":\"kryvoozerskyi\",\"rus\":\"Кривоозерский район\"}],\"odesa\":[{\"slug\":\"berezivskyi\",\"rus\":\"Березовский район\"},{\"slug\":\"mykolaivskyi\",\"rus\":\"Николаевский район\"},{\"slug\":\"shyriaiivskyi\",\"rus\":\"Ширяевский район\"},{\"slug\":\"bilhorod-dnistrovskyi\",\"rus\":\"Белгород-Днестровский район\"},{\"slug\":\"saratovskyi\",\"rus\":\"Саратский район\"},{\"slug\":\"tatarbunarskyi\",\"rus\":\"Татарбунарский район\"},{\"slug\":\"bolhradskyi\",\"rus\":\"Болградский район\"},{\"slug\":\"artsyzkyi\",\"rus\":\"Арцизский район\"},{\"slug\":\"tarutynskyi\",\"rus\":\"Тарутинский район\"},{\"slug\":\"izmailskyi\",\"rus\":\"Измаильский район\"},{\"slug\":\"vylkove\",\"rus\":\"Вилково\"},{\"slug\":\"kiliiskyi\",\"rus\":\"Килийский район\"},{\"slug\":\"reniyskyi\",\"rus\":\"Ренийский район\"},{\"slug\":\"odeskyi\",\"rus\":\"Одесский район\"},{\"slug\":\"biliaivskyi\",\"rus\":\"Беляевский район\"},{\"slug\":\"ovidiopolskyi\",\"rus\":\"Овидиопольский район\"},{\"slug\":\"chornomorskyi\",\"rus\":\"Черноморский район\"},{\"slug\":\"yuzhne\",\"rus\":\"Южное\"},{\"slug\":\"podilskyi\",\"rus\":\"Подольский район\"},{\"slug\":\"ananiivskyi\",\"rus\":\"Ананьевский район\"},{\"slug\":\"baltskyi\",\"rus\":\"Балтский район\"},{\"slug\":\"kodymskyi\",\"rus\":\"Кодымский район\"},{\"slug\":\"liubashivskyi\",\"rus\":\"Любашёвский район\"},{\"slug\":\"oknianskyi\",\"rus\":\"Окнянский район\"},{\"slug\":\"savranskyi\",\"rus\":\"Савранский район\"},{\"slug\":\"rozdilnianskyi\",\"rus\":\"Раздельнянский район\"},{\"slug\":\"velykomikhaylivskyi\",\"rus\":\"Великомихайловский район\"},{\"slug\":\"zakhariivskyi\",\"rus\":\"Захарьевский район\"},{\"slug\":\"lymanskyi\",\"rus\":\"Лиманский район\"}],\"poltava\":[{\"slug\":\"poltava\",\"rus\":\"Полтавский район\"},{\"slug\":\"dikanka\",\"rus\":\"Диканьский район\"},{\"slug\":\"zinkiv\",\"rus\":\"Зеньковский район\"},{\"slug\":\"karlivka\",\"rus\":\"Карловский район\"},{\"slug\":\"kobelyaki\",\"rus\":\"Кобелякский район\"},{\"slug\":\"kotelva\",\"rus\":\"Котелевский район\"},{\"slug\":\"novi-sanzhary\",\"rus\":\"Новосанжарский район\"},{\"slug\":\"opishnya\",\"rus\":\"Опошня\"},{\"slug\":\"reshetylivka\",\"rus\":\"Решетиловский район\"},{\"slug\":\"kremenchuk\",\"rus\":\"Кременчугский район\"},{\"slug\":\"globino\",\"rus\":\"Глобинский район\"},{\"slug\":\"gorishni-plavni\",\"rus\":\"Горишние Плавни\"},{\"slug\":\"mirgorod\",\"rus\":\"Миргородский район\"},{\"slug\":\"gadyach\",\"rus\":\"Гадячский район\"},{\"slug\":\"gogolivske\",\"rus\":\"Гоголевское\"},{\"slug\":\"lokhvytsia\",\"rus\":\"Лохвицкий район\"},{\"slug\":\"romodanivske\",\"rus\":\"Ромодановское\"},{\"slug\":\"shishaki\",\"rus\":\"Шишацкий район\"},{\"slug\":\"lubny\",\"rus\":\"Лубенский район\"},{\"slug\":\"hrebinka\",\"rus\":\"Гребенковский район\"},{\"slug\":\"pyryatyn\",\"rus\":\"Пирятинский район\"},{\"slug\":\"khorol\",\"rus\":\"Хорольский район\"},{\"slug\":\"chornukhine\",\"rus\":\"Чернухинский район\"}],\"rivne\":[{\"slug\":\"varaskyi\",\"rus\":\"Варашский район\"},{\"slug\":\"volodymyrets\",\"rus\":\"Владимирецкий район\"},{\"slug\":\"zarichnenskyi\",\"rus\":\"Заречненский район\"},{\"slug\":\"dubenskyi\",\"rus\":\"Дубенский район\"},{\"slug\":\"demidivskyi\",\"rus\":\"Демидовский район\"},{\"slug\":\"mlynivskyi\",\"rus\":\"Млиновский район\"},{\"slug\":\"radivilivskyi\",\"rus\":\"Радивиловский район\"},{\"slug\":\"rivnenskyi\",\"rus\":\"Ровенский район\"},{\"slug\":\"bereznivskyi\",\"rus\":\"Березновский район\"},{\"slug\":\"hoshchanskyi\",\"rus\":\"Гощанский район\"},{\"slug\":\"zdolbunivskyi\",\"rus\":\"Здолбуновский район\"},{\"slug\":\"klevan\",\"rus\":\"Клевань\"},{\"slug\":\"koretskyi\",\"rus\":\"Корецкий район\"},{\"slug\":\"kostopilskyi\",\"rus\":\"Костопольский район\"},{\"slug\":\"ostrozkyi\",\"rus\":\"Острожский район\"},{\"slug\":\"sarnenskyi\",\"rus\":\"Сарненский район\"},{\"slug\":\"dubrovitskyi\",\"rus\":\"Дубровицкий район\"},{\"slug\":\"rokytnivskyi\",\"rus\":\"Рокитновский район\"},{\"slug\":\"klesiv\",\"rus\":\"Клесов\"}],\"sumy\":[{\"slug\":\"konotopskyi\",\"rus\":\"Конотопский район\"},{\"slug\":\"burynskyi\",\"rus\":\"Бурынский район\"},{\"slug\":\"krolevetskyi\",\"rus\":\"Кролевецкий район\"},{\"slug\":\"putivlskyi\",\"rus\":\"Путивльский район\"},{\"slug\":\"okhtyrskyi\",\"rus\":\"Ахтырский район\"},{\"slug\":\"velykopysarivskyi\",\"rus\":\"Великописаревский район\"},{\"slug\":\"trostyanetskyi\",\"rus\":\"Тростянецкий район\"},{\"slug\":\"romenskyi\",\"rus\":\"Роменский район\"},{\"slug\":\"lypovodynskyi\",\"rus\":\"Липоводолинский район\"},{\"slug\":\"nedryhaylivskyi\",\"rus\":\"Недригайловский район\"},{\"slug\":\"sumskyi\",\"rus\":\"Сумской район\"},{\"slug\":\"bilopilskyi\",\"rus\":\"Белопольский район\"},{\"slug\":\"krasnopilskyi\",\"rus\":\"Краснопольский район\"},{\"slug\":\"lebedynskyi\",\"rus\":\"Лебединский район\"},{\"slug\":\"shostkinskyi\",\"rus\":\"Шосткинский район\"},{\"slug\":\"hlukhivskyi\",\"rus\":\"Глуховский район\"},{\"slug\":\"seredynobudskyi\",\"rus\":\"Середино-Будский район\"},{\"slug\":\"yampilskyi\",\"rus\":\"Ямпольский район\"}],\"ternopil\":[{\"slug\":\"kremenetskyy\",\"rus\":\"Кременецкий район\"},{\"slug\":\"lanovetskyy\",\"rus\":\"Лановецкий район\"},{\"slug\":\"pochaiv\",\"rus\":\"город Почаев\"},{\"slug\":\"shumskyy\",\"rus\":\"Шумский район\"},{\"slug\":\"ternopilskyy\",\"rus\":\"Тернопольский район\"},{\"slug\":\"berezhanskyy\",\"rus\":\"Бережанский район\"},{\"slug\":\"zbarazkyy\",\"rus\":\"Збаражский район\"},{\"slug\":\"zborivskyy\",\"rus\":\"Зборовский район\"},{\"slug\":\"pidvolochyskyy\",\"rus\":\"Подволочисский район\"},{\"slug\":\"pidhayetskyy\",\"rus\":\"Подгаецкий район\"},{\"slug\":\"terebovlyanskyy\",\"rus\":\"Теребовлянский район\"},{\"slug\":\"chortkivskyy\",\"rus\":\"Чортковский район\"},{\"slug\":\"borschivskyy\",\"rus\":\"Борщёвский район\"},{\"slug\":\"buchatskyy\",\"rus\":\"Бучачский район\"},{\"slug\":\"husyatynskyy\",\"rus\":\"Гусятинский район\"},{\"slug\":\"zalishchytskyy\",\"rus\":\"Залещицкий район\"},{\"slug\":\"monastyryskyy\",\"rus\":\"Монастырский район\"}],\"kharkiv\":[{\"slug\":\"bohodukhivskyy\",\"rus\":\"Богодуховский район\"},{\"slug\":\"valkivskyy\",\"rus\":\"Валковский район\"},{\"slug\":\"zolochivskyy\",\"rus\":\"Золочевский район\"},{\"slug\":\"kolomatskyy\",\"rus\":\"Коломакский район\"},{\"slug\":\"krasnokutskyy\",\"rus\":\"Краснокутский район\"},{\"slug\":\"izyumskyy\",\"rus\":\"Изюмский район\"},{\"slug\":\"balakliyskyy\",\"rus\":\"Балаклейский район\"},{\"slug\":\"barvinkivskyy\",\"rus\":\"Барвенковский район\"},{\"slug\":\"borivskyy\",\"rus\":\"Боровский район\"},{\"slug\":\"krasnogradskyy\",\"rus\":\"Красноградский район\"},{\"slug\":\"zachepylivskyy\",\"rus\":\"Зачепиловский район\"},{\"slug\":\"kehychivskyy\",\"rus\":\"Кегичевский район\"},{\"slug\":\"sakhnovshchynskyy\",\"rus\":\"Сахновщинский район\"},{\"slug\":\"kupyanskyy\",\"rus\":\"Купянский район\"},{\"slug\":\"velykoburlutska\",\"rus\":\"Великобурлукский район\"},{\"slug\":\"dvorichanskyy\",\"rus\":\"Двуречанский район\"},{\"slug\":\"shevchenkivskyy\",\"rus\":\"Шевченковский район\"},{\"slug\":\"lozivskyy\",\"rus\":\"Лозовский район\"},{\"slug\":\"blyznyukivskyy\",\"rus\":\"Близнюковский район\"},{\"slug\":\"permyakivskyy\",\"rus\":\"Первомайский район\"},{\"slug\":\"kharkivskyy\",\"rus\":\"Харьковский район\"},{\"slug\":\"derhachivskyy\",\"rus\":\"Дергачёвский район\"},{\"slug\":\"liubotynskyy\",\"rus\":\"Люботинский район\"},{\"slug\":\"novovodolazkyy\",\"rus\":\"Нововодолажский район\"},{\"slug\":\"chuhuyivskyy\",\"rus\":\"Чугуевский район\"},{\"slug\":\"vovchanskyy\",\"rus\":\"Волчанский район\"},{\"slug\":\"zmiiivskyy\",\"rus\":\"Змиевской район\"},{\"slug\":\"pechenizkyy\",\"rus\":\"Печенежский район\"}],\"kherson\":[{\"slug\":\"berislavskyy\",\"rus\":\"Бериславский район\"},{\"slug\":\"velykookseandrivskyy\",\"rus\":\"Великоалександровский район\"},{\"slug\":\"vysokopilskyy\",\"rus\":\"Высокопольский район\"},{\"slug\":\"novovorontsovskyy\",\"rus\":\"Нововоронцовский район\"},{\"slug\":\"henicheskyy\",\"rus\":\"Генический район\"},{\"slug\":\"ivanivskyy\",\"rus\":\"Ивановский район\"},{\"slug\":\"nizhnosirohozskyy\",\"rus\":\"Нижнесерогозский район\"},{\"slug\":\"novotroitskyy\",\"rus\":\"Новотроицкий район\"},{\"slug\":\"kakhovskyy\",\"rus\":\"Каховский район\"},{\"slug\":\"velykolepetyskyy\",\"rus\":\"Великолепетихский район\"},{\"slug\":\"verkhnyorohachytskyy\",\"rus\":\"Верхнерогачикский район\"},{\"slug\":\"hornostayivskyy\",\"rus\":\"Горностаевский район\"},{\"slug\":\"chaplynskyy\",\"rus\":\"Чаплинский район\"},{\"slug\":\"nova-kakhovka\",\"rus\":\"Новая Каховка\"},{\"slug\":\"skadovskyy\",\"rus\":\"Скадовский район\"},{\"slug\":\"holoprystanskyy\",\"rus\":\"Голопристанский район\"},{\"slug\":\"kalanchatskyy\",\"rus\":\"Каланчакский район\"},{\"slug\":\"khersonskyy\",\"rus\":\"Херсонский район\"},{\"slug\":\"bilozeretskyy\",\"rus\":\"Белозёрский район\"},{\"slug\":\"oleshkivskyy\",\"rus\":\"Олешковский район\"}],\"khmelnytskyi\":[{\"slug\":\"kamyanets-podilskyy\",\"rus\":\"Каменец-Подольский район\"},{\"slug\":\"dunayevetskyy\",\"rus\":\"Дунаевецкий район\"},{\"slug\":\"novoushytskyy\",\"rus\":\"Новоушицкий район\"},{\"slug\":\"chemerovetskyy\",\"rus\":\"Чемеровецкий район\"},{\"slug\":\"khmelnytskyy\",\"rus\":\"Хмельницкий район\"},{\"slug\":\"vinkovetskyy\",\"rus\":\"Виньковецкий район\"},{\"slug\":\"volochyskyy\",\"rus\":\"Волочисский район\"},{\"slug\":\"horodotskyy\",\"rus\":\"Городокский район\"},{\"slug\":\"derazhnyanskyy\",\"rus\":\"Деражнянский район\"},{\"slug\":\"krasylivskyy\",\"rus\":\"Красиловский район\"},{\"slug\":\"letychivskyy\",\"rus\":\"Летичевский район\"},{\"slug\":\"starokostyantynivskyy\",\"rus\":\"Староконстантиновский район\"},{\"slug\":\"starosyniavskyy\",\"rus\":\"Старосинявский район\"},{\"slug\":\"teofipolksyy\",\"rus\":\"Теофипольский район\"},{\"slug\":\"yarmolyntsiyskyy\",\"rus\":\"Ярмолинецкий район\"},{\"slug\":\"shepetivskyy\",\"rus\":\"Шепетовский район\"},{\"slug\":\"bilohirskyy\",\"rus\":\"Белогорский район\"},{\"slug\":\"izyaslavskyy\",\"rus\":\"Изяславский район\"},{\"slug\":\"slavutskyy\",\"rus\":\"Славутский район\"},{\"slug\":\"netishyn\",\"rus\":\"Нетешин\"}],\"cherkasy\":[{\"slug\":\"zvenyhorodskyy\",\"rus\":\"Звенигородский район\"},{\"slug\":\"vatutine\",\"rus\":\"Ватутино\"},{\"slug\":\"katerynopilskyy\",\"rus\":\"Катеринопольский район\"},{\"slug\":\"lysianskyy\",\"rus\":\"Лысянский район\"},{\"slug\":\"talnivskyy\",\"rus\":\"Тальновский район\"},{\"slug\":\"shpolyanskyy\",\"rus\":\"Шполянский район\"},{\"slug\":\"zolotonoshskyy\",\"rus\":\"Золотоношский район\"},{\"slug\":\"drabivskyy\",\"rus\":\"Драбовский район\"},{\"slug\":\"chornobayivskyy\",\"rus\":\"Чернобаевский район\"},{\"slug\":\"umanskyy\",\"rus\":\"Уманский район\"},{\"slug\":\"zhashkivskyy\",\"rus\":\"Жашковский район\"},{\"slug\":\"mankivskyy\",\"rus\":\"Маньковский район\"},{\"slug\":\"monastyryshchenskyy\",\"rus\":\"Монастырищенский район\"},{\"slug\":\"khrystynivskyy\",\"rus\":\"Христиновский район\"},{\"slug\":\"cherkaskyy\",\"rus\":\"Черкасский район\"},{\"slug\":\"horodyshchenskyy\",\"rus\":\"Городищенский район\"},{\"slug\":\"kamyanetskyy\",\"rus\":\"Каменский район\"},{\"slug\":\"kanivskyy\",\"rus\":\"Каневский район\"},{\"slug\":\"korsun-shevchenkivskyy\",\"rus\":\"Корсунь-Шевченковский район\"},{\"slug\":\"smilyanskyy\",\"rus\":\"Смелянский район\"},{\"slug\":\"chyhyrynskyy\",\"rus\":\"Чигиринский район\"}],\"chernihiv\":[{\"slug\":\"koriukivskyy\",\"rus\":\"Корюковский район\"},{\"slug\":\"menskyy\",\"rus\":\"Менский район\"},{\"slug\":\"snovskyy\",\"rus\":\"Сновский район\"},{\"slug\":\"sosnytskyy\",\"rus\":\"Сосницкий район\"},{\"slug\":\"nizhynskyy\",\"rus\":\"Нежинский район\"},{\"slug\":\"baturyn\",\"rus\":\"Батурин\"},{\"slug\":\"bakhmatskyy\",\"rus\":\"Бахмачский район\"},{\"slug\":\"bobrovytskyy\",\"rus\":\"Бобровицкий район\"},{\"slug\":\"borznyanskyy\",\"rus\":\"Борзнянский район\"},{\"slug\":\"nosivskyy\",\"rus\":\"Носовский район\"},{\"slug\":\"novhorod-siverskyy\",\"rus\":\"Новгород-Северский район\"},{\"slug\":\"koropskyy\",\"rus\":\"Коропский район\"},{\"slug\":\"semenivskyy\",\"rus\":\"Семёновский район\"},{\"slug\":\"prylutskyy\",\"rus\":\"Прилукский район\"},{\"slug\":\"varvynskyy\",\"rus\":\"Варвинский район\"},{\"slug\":\"ichnyanskyy\",\"rus\":\"Ичнянский район\"},{\"slug\":\"sribnyanskyy\",\"rus\":\"Сребнянский район\"},{\"slug\":\"talalaivskyy\",\"rus\":\"Талалаевский район\"},{\"slug\":\"chernihivskyy\",\"rus\":\"Черниговский район\"},{\"slug\":\"horodnyanskyy\",\"rus\":\"Городнянский район\"},{\"slug\":\"kozeletskyy\",\"rus\":\"Козелецкий район\"},{\"slug\":\"kulikivskyy\",\"rus\":\"Куликовский район\"},{\"slug\":\"ripkynskyy\",\"rus\":\"Репкинский район\"}],\"chernivtsi\":[{\"slug\":\"vyzhnytskyy\",\"rus\":\"Вижницкий район\"},{\"slug\":\"putylskyy\",\"rus\":\"Путильский район\"},{\"slug\":\"dnistrovskyy\",\"rus\":\"Днестровский район\"},{\"slug\":\"novodnistrovsk\",\"rus\":\"Новоднестровск\"},{\"slug\":\"sokyryanskyy\",\"rus\":\"Сокирянский район\"},{\"slug\":\"khotynskyy\",\"rus\":\"Хотинский район\"},{\"slug\":\"chernivetskyy\",\"rus\":\"Черновецкий район\"},{\"slug\":\"herchayivskyy\",\"rus\":\"Герцаевский район\"},{\"slug\":\"hlybotskyy\",\"rus\":\"Глыбокский район\"},{\"slug\":\"zastavnyvskyy\",\"rus\":\"Заставновский район\"},{\"slug\":\"kitsmanskyy\",\"rus\":\"Кицманский район\"},{\"slug\":\"novoselytskyy\",\"rus\":\"Новоселицкий район\"},{\"slug\":\"storozhynetskyy\",\"rus\":\"Сторожинецкий район\"}],\"krym\":[{\"slug\":\"bakhchisarayskyy\",\"rus\":\"Бахчисарайский район\"},{\"slug\":\"bilohirskyy\",\"rus\":\"Белогорский район\"},{\"slug\":\"nizhnogirskyy\",\"rus\":\"Нижнегорский район\"},{\"slug\":\"djankoy\",\"rus\":\"Джанкойский район\"},{\"slug\":\"yevpatoriyskyy\",\"rus\":\"Евпатория\"},{\"slug\":\"chornomorskyy\",\"rus\":\"Черноморский район\"},{\"slug\":\"sakskyy\",\"rus\":\"Сакский район\"},{\"slug\":\"kerchenskyy\",\"rus\":\"Керченский район\"},{\"slug\":\"edykuyskyy\",\"rus\":\"Едикуйский район\"},{\"slug\":\"kurmanskyy\",\"rus\":\"Курманский район\"},{\"slug\":\"pervomayskyy\",\"rus\":\"Первомайский район\"},{\"slug\":\"perekopskyy\",\"rus\":\"Перекопский район\"},{\"slug\":\"rozdolnenskyy\",\"rus\":\"Раздольненский район\"},{\"slug\":\"simferopolskyy\",\"rus\":\"Симферопольский район\"},{\"slug\":\"feodosiyskyy\",\"rus\":\"Феодосийский район\"},{\"slug\":\"sudak\",\"rus\":\"Судак\"},{\"slug\":\"feodosiya\",\"rus\":\"Феодосия\"},{\"slug\":\"islamteretskyy\",\"rus\":\"Ислям-Терекский район\"},{\"slug\":\"ichkinskyy\",\"rus\":\"Ичкинский район\"},{\"slug\":\"yaltyntskyy\",\"rus\":\"Ялта\"},{\"slug\":\"alushta\",\"rus\":\"Алушта\"}]}";
        String json = "{\"vinnycya\":[{\"slug\":\"vinnitsa\",\"rus\":\"Винницкий район\"},{\"slug\":\"illinetsky\",\"rus\":\"Ильинецкий район\"},{\"slug\":\"lypovetsky\",\"rus\":\"Липовецкий район\"},{\"slug\":\"litinsky\",\"rus\":\"Литинский район\"},{\"slug\":\"nemirovsky\",\"rus\":\"Немировский район\"},{\"slug\":\"orativ\",\"rus\":\"Оратовский район\"},{\"slug\":\"pohrebishche\",\"rus\":\"Погребищенский район\"},{\"slug\":\"tyvriv\",\"rus\":\"Тывровский район\"},{\"slug\":\"gaissin\",\"rus\":\"Гайсинский район\"},{\"slug\":\"bershad\",\"rus\":\"Бершадский район\"},{\"slug\":\"teplichi\",\"rus\":\"Теплицкий район\"},{\"slug\":\"trostyanets\",\"rus\":\"Тростянецкий район\"},{\"slug\":\"chechelnytsky\",\"rus\":\"Чечельницкий район\"},{\"slug\":\"zhmerynka\",\"rus\":\"Жмеринский район\"},{\"slug\":\"barsky\",\"rus\":\"Барский район\"},{\"slug\":\"shargorod\",\"rus\":\"Шаргородский район\"},{\"slug\":\"mohyliv-podilsky\",\"rus\":\"Могилёв-Подольский район\"},{\"slug\":\"murovankurilovetsky\",\"rus\":\"Мурованокуриловецкий район\"},{\"slug\":\"chernive\",\"rus\":\"Черновицкий район\"},{\"slug\":\"yampil\",\"rus\":\"Ямпольский район\"},{\"slug\":\"tulchyn\",\"rus\":\"Тульчинский район\"},{\"slug\":\"kryzhopilsky\",\"rus\":\"Крыжопольский район\"},{\"slug\":\"pishchansky\",\"rus\":\"Песчанский район\"},{\"slug\":\"tomashpilsky\",\"rus\":\"Томашпольский район\"},{\"slug\":\"khmilnytsky\",\"rus\":\"Хмельницкий район\"},{\"slug\":\"kalynivsky\",\"rus\":\"Калиновский район\"},{\"slug\":\"kozyatyn\",\"rus\":\"Казатинский район\"}],\"volyn\":[{\"slug\":\"volodymyr-volynsky\",\"rus\":\"Владимир-Волынский район\"},{\"slug\":\"ivanychivsky\",\"rus\":\"Иваничевский район\"},{\"slug\":\"lokachynsky\",\"rus\":\"Локачинский район\"},{\"slug\":\"kamin-kashyrsky\",\"rus\":\"Камень-Каширский район\"},{\"slug\":\"liubeshivsky\",\"rus\":\"Любешевский район\"},{\"slug\":\"manevytsky\",\"rus\":\"Маневичский район\"},{\"slug\":\"kovel\",\"rus\":\"Ковельский район\"},{\"slug\":\"liubomlsky\",\"rus\":\"Любомльский район\"},{\"slug\":\"ratnivsky\",\"rus\":\"Ратновский район\"},{\"slug\":\"starovyzhivsky\",\"rus\":\"Старовыжевский район\"},{\"slug\":\"turiysky\",\"rus\":\"Турийский район\"},{\"slug\":\"shatsky\",\"rus\":\"Шацкий район\"},{\"slug\":\"lutsk\",\"rus\":\"Луцкий район\"},{\"slug\":\"horokhivsky\",\"rus\":\"Гороховский район\"},{\"slug\":\"kivertsivsky\",\"rus\":\"Киверцовский район\"},{\"slug\":\"rozhysche\",\"rus\":\"Рожищенский район\"}],\"dnipro\":[{\"slug\":\"dniprovsky\",\"rus\":\"Днепровский район\"},{\"slug\":\"petrikivsky\",\"rus\":\"Петриковский район\"},{\"slug\":\"tsarychansky\",\"rus\":\"Царичанский район\"},{\"slug\":\"solonyansky\",\"rus\":\"Солонянский район\"},{\"slug\":\"verkhnedniprovsky\",\"rus\":\"Верхнеднепровский район\"},{\"slug\":\"krynychansky\",\"rus\":\"Криничанский район\"},{\"slug\":\"piatykhatky\",\"rus\":\"Пятихатский район\"},{\"slug\":\"krivorizky\",\"rus\":\"Криворожский район\"},{\"slug\":\"apostolivsky\",\"rus\":\"Апостоловский район\"},{\"slug\":\"sofiyivsky\",\"rus\":\"Софиевский район\"},{\"slug\":\"shyrokivsky\",\"rus\":\"Широковский район\"},{\"slug\":\"nikopolsky\",\"rus\":\"Никопольский район\"},{\"slug\":\"tomakivsky\",\"rus\":\"Томаковский район\"},{\"slug\":\"novomoskovsky\",\"rus\":\"Новомосковский район\"},{\"slug\":\"magdalinivsky\",\"rus\":\"Магдалиновский район\"},{\"slug\":\"pavlogradsky\",\"rus\":\"Павлоградский район\"},{\"slug\":\"yuriivsky\",\"rus\":\"Юрьевский район\"},{\"slug\":\"sinyelnikivsky\",\"rus\":\"Синельниковский район\"},{\"slug\":\"vasylkivsky\",\"rus\":\"Васильковский район\"},{\"slug\":\"mezhivsky\",\"rus\":\"Межевский район\"},{\"slug\":\"petropavlivsky\",\"rus\":\"Петропавловский район\"},{\"slug\":\"pokrovsky\",\"rus\":\"Покровский район\"}],\"donetsk\":[{\"slug\":\"bakhmutske\",\"rus\":\"Бахмутский район\"},{\"slug\":\"volnovaske\",\"rus\":\"Волновахский район\"},{\"slug\":\"velikonovosilkivske\",\"rus\":\"Великоновоселковский район\"},{\"slug\":\"shakhtarske\",\"rus\":\"Шахтерский район\"},{\"slug\":\"amvrosiivske\",\"rus\":\"Амвросиевский район\"},{\"slug\":\"yasinuvatske\",\"rus\":\"Ясиноватский район\"},{\"slug\":\"telmanovsky\",\"rus\":\"Тельмановский район\"},{\"slug\":\"novoazovske\",\"rus\":\"Новоазовский район\"},{\"slug\":\"starobeshivske\",\"rus\":\"Старобешевский район\"},{\"slug\":\"kostyantynivske\",\"rus\":\"Константиновский район\"},{\"slug\":\"lymanske\",\"rus\":\"Лиманский район\"},{\"slug\":\"oleksandrivske\",\"rus\":\"Александровский район\"},{\"slug\":\"slovyanske\",\"rus\":\"Славянский район\"},{\"slug\":\"mariupolske\",\"rus\":\"Мариупольский\"},{\"slug\":\"pershotravnevy\",\"rus\":\"Першотравневый район\"},{\"slug\":\"nikolske\",\"rus\":\"Никольский район\"},{\"slug\":\"pokrovske\",\"rus\":\"Покровский район\"},{\"slug\":\"dobropilsky\",\"rus\":\"Добропольский район\"},{\"slug\":\"maryinka\",\"rus\":\"Марьинский район\"}],\"zhytomyr\":[{\"slug\":\"berdychivsky\",\"rus\":\"Бердичевский район\"},{\"slug\":\"andrushivsky\",\"rus\":\"Андрушёвский район\"},{\"slug\":\"ruzhynsky\",\"rus\":\"Ружинский район\"},{\"slug\":\"zhytomyrsky\",\"rus\":\"Житомирский район\"},{\"slug\":\"brusylivsky\",\"rus\":\"Брусиловский район\"},{\"slug\":\"korostyshivsky\",\"rus\":\"Коростышевский район\"},{\"slug\":\"liubarsky\",\"rus\":\"Любарский район\"},{\"slug\":\"popilniansky\",\"rus\":\"Попельнянский район\"},{\"slug\":\"pulynsky\",\"rus\":\"Пулинский район\"},{\"slug\":\"radomyshlsky\",\"rus\":\"Радомышльский район\"},{\"slug\":\"romanivsky\",\"rus\":\"Романовский район\"},{\"slug\":\"khoroshivsky\",\"rus\":\"Хорошевский район\"},{\"slug\":\"chernyakhivsky\",\"rus\":\"Черняховский район\"},{\"slug\":\"chudnivsky\",\"rus\":\"Чудновский район\"},{\"slug\":\"korostensky\",\"rus\":\"Коростенский район\"},{\"slug\":\"luhynsky\",\"rus\":\"Лугинский район\"},{\"slug\":\"malynsky\",\"rus\":\"Малинский район\"},{\"slug\":\"narodytsky\",\"rus\":\"Народичский район\"},{\"slug\":\"ovrutsky\",\"rus\":\"Овручский район\"},{\"slug\":\"olevsky\",\"rus\":\"Олевский район\"},{\"slug\":\"novohrad-volynsky\",\"rus\":\"Новоград-Волынский район\"},{\"slug\":\"baranivsky\",\"rus\":\"Барановский район\"},{\"slug\":\"yemilchynsky\",\"rus\":\"Емильчинский район\"}],\"zakarpattya\":[{\"slug\":\"berehivsky\",\"rus\":\"Береговский район\"},{\"slug\":\"vinohradivsky\",\"rus\":\"Виноградовский район\"},{\"slug\":\"mukachivsky\",\"rus\":\"Мукачевский район\"},{\"slug\":\"volovetsky\",\"rus\":\"Воловецкий район\"},{\"slug\":\"svaliavsky\",\"rus\":\"Свалявский район\"},{\"slug\":\"rakhivsky\",\"rus\":\"Раховский район\"},{\"slug\":\"tyachivsky\",\"rus\":\"Тячевский район\"},{\"slug\":\"uzhhorodsky\",\"rus\":\"Ужгородский район\"},{\"slug\":\"velykoberizniansky\",\"rus\":\"Великоберезнянский район\"},{\"slug\":\"perechynsky\",\"rus\":\"Перечинский район\"},{\"slug\":\"khustsky\",\"rus\":\"Хустский район\"},{\"slug\":\"irshavsky\",\"rus\":\"Иршавский район\"},{\"slug\":\"mizhhirsky\",\"rus\":\"Межгорский район\"}],\"zaporizhzhya\":[{\"slug\":\"berdiansky\",\"rus\":\"Бердянский район\"},{\"slug\":\"prymorsky\",\"rus\":\"Приморский район\"},{\"slug\":\"chernihivsky\",\"rus\":\"Черниговский район\"},{\"slug\":\"vasylivsky\",\"rus\":\"Васильевский район\"},{\"slug\":\"kamiansko-dniprovsky\",\"rus\":\"Каменско-Днепровский район\"},{\"slug\":\"velykobilozersky\",\"rus\":\"Великобелозёрский район\"},{\"slug\":\"mykhailivsky\",\"rus\":\"Михайловский район\"},{\"slug\":\"zaporizky\",\"rus\":\"Запорожский район\"},{\"slug\":\"zaporizzya\",\"rus\":\"Запорожье\"},{\"slug\":\"vilniansky\",\"rus\":\"Вольнянский район\"},{\"slug\":\"novomykolaivsky\",\"rus\":\"Новониколаевский район\"},{\"slug\":\"melitopolsky\",\"rus\":\"Мелитопольский район\"},{\"slug\":\"veselivsky\",\"rus\":\"Весёловский район\"},{\"slug\":\"priazovsky\",\"rus\":\"Приазовский район\"},{\"slug\":\"iakymivsky\",\"rus\":\"Якимовский район\"},{\"slug\":\"pologivsky\",\"rus\":\"Пологовский район\"},{\"slug\":\"bilomatsky\",\"rus\":\"Бильмакский район\"},{\"slug\":\"huliaypilsky\",\"rus\":\"Гуляйпольский район\"},{\"slug\":\"orikhivsky\",\"rus\":\"Ореховский район\"},{\"slug\":\"rozivsky\",\"rus\":\"Розовский район\"},{\"slug\":\"tokmatsky\",\"rus\":\"Токмакский район\"}],\"ivano-frankivsk\":[{\"slug\":\"verkhovynsky\",\"rus\":\"Верховинский район\"},{\"slug\":\"bohorodchansky\",\"rus\":\"Богородчанский район\"},{\"slug\":\"halytsky\",\"rus\":\"Галичский район\"},{\"slug\":\"rohatynsky\",\"rus\":\"Рогатинский район\"},{\"slug\":\"tysmenytsky\",\"rus\":\"Тысменицкий район\"},{\"slug\":\"tlumatsky\",\"rus\":\"Тлумачский район\"},{\"slug\":\"kalusky\",\"rus\":\"Калушский район\"},{\"slug\":\"dolynsky\",\"rus\":\"Долинский район\"},{\"slug\":\"rozhnyativsky\",\"rus\":\"Рожнятовский район\"},{\"slug\":\"kolomyisky\",\"rus\":\"Коломыйский район\"},{\"slug\":\"horodenkivsky\",\"rus\":\"Городенковский район\"},{\"slug\":\"sniatynsky\",\"rus\":\"Снятынский район\"},{\"slug\":\"kosivsky\",\"rus\":\"Косовский район\"},{\"slug\":\"nadvirnyansky\",\"rus\":\"Надворнянский район\"}],\"kyiv\":[{\"slug\":\"bilotserkivsky\",\"rus\":\"Белоцерковский район\"},{\"slug\":\"volodarsky\",\"rus\":\"Володарский район\"},{\"slug\":\"rokytnyansky\",\"rus\":\"Ракитнянский район\"},{\"slug\":\"skvyrsky\",\"rus\":\"Сквирский район\"},{\"slug\":\"stavishchensky\",\"rus\":\"Ставищенский район\"},{\"slug\":\"tarashchansky\",\"rus\":\"Таращанский район\"},{\"slug\":\"tetievsky\",\"rus\":\"Тетиевский район\"},{\"slug\":\"boryspilsky\",\"rus\":\"Бориспольский район\"},{\"slug\":\"peryaslavsky\",\"rus\":\"Переяслав-Хмельницкий район\"},{\"slug\":\"yahotynsky\",\"rus\":\"Яготинский район\"},{\"slug\":\"brovarsky\",\"rus\":\"Броварский район\"},{\"slug\":\"baryshivsky\",\"rus\":\"Барышевский район\"},{\"slug\":\"zghurivsky\",\"rus\":\"Згуровский район\"},{\"slug\":\"borodyansky\",\"rus\":\"Бородянский район\"},{\"slug\":\"makarivsky\",\"rus\":\"Макаровский район\"},{\"slug\":\"vyshhorodsky\",\"rus\":\"Вышгородский район\"},{\"slug\":\"ivankivsky\",\"rus\":\"Иванковский район\"},{\"slug\":\"polissky\",\"rus\":\"Полесский район\"},{\"slug\":\"obukhivsky\",\"rus\":\"Обуховский район\"},{\"slug\":\"bohuslavsky\",\"rus\":\"Богуславский район\"},{\"slug\":\"vasylkivsky\",\"rus\":\"Васильковский район\"},{\"slug\":\"kaharlytsky\",\"rus\":\"Кагарлыкский район\"},{\"slug\":\"myronivsky\",\"rus\":\"Мироновский район\"},{\"slug\":\"fastivsky\",\"rus\":\"Фастовский район\"},{\"slug\":\"kyevosvyatoshynsky\",\"rus\":\"Киево-Святошинский район\"}],\"kropyvnytskyi\":[{\"slug\":\"holovanivsky\",\"rus\":\"Голованевский район\"},{\"slug\":\"blahovishchensky\",\"rus\":\"Благовещенский район\"},{\"slug\":\"vilshansky\",\"rus\":\"Ольшанский район\"},{\"slug\":\"haivoronsky\",\"rus\":\"Гайворонский район\"},{\"slug\":\"novoarkhanhelsky\",\"rus\":\"Новоархангельский район\"},{\"slug\":\"kropyvnytskyi\",\"rus\":\"Кропивницкий район\"},{\"slug\":\"bobrynetsky\",\"rus\":\"Бобринецкий район\"},{\"slug\":\"dolynsky\",\"rus\":\"Долинский район\"},{\"slug\":\"znamiansky\",\"rus\":\"Знаменский район\"},{\"slug\":\"kompaniivsky\",\"rus\":\"Компанеевский район\"},{\"slug\":\"novhorodkivsky\",\"rus\":\"Новгородковский район\"},{\"slug\":\"oleksandrivskyi\",\"rus\":\"Александрийский район\"},{\"slug\":\"ustinovsky\",\"rus\":\"Устиновский район\"},{\"slug\":\"novoukrainsky\",\"rus\":\"Новоукраинский район\"},{\"slug\":\"dobrovelychkivsky\",\"rus\":\"Добровеличковский район\"},{\"slug\":\"malovyskivsky\",\"rus\":\"Маловисковский район\"},{\"slug\":\"novomyrhorodsky\",\"rus\":\"Новомиргородский район\"},{\"slug\":\"onufriivsky\",\"rus\":\"Онуфриевский район\"},{\"slug\":\"petrivsky\",\"rus\":\"Петровский район\"},{\"slug\":\"svitlovodsky\",\"rus\":\"Светловодский район\"}],\"lugansk\":[{\"slug\":\"luhanskyi\",\"rus\":\"Луганский район\"},{\"slug\":\"lutuhine\",\"rus\":\"Лутугинский район\"},{\"slug\":\"rovenkivskyi\",\"rus\":\"Ровеньковский район\"},{\"slug\":\"antratsyt\",\"rus\":\"Антрацитовский район\"},{\"slug\":\"svativskyi\",\"rus\":\"Сватовский район\"},{\"slug\":\"bilokurakynskyi\",\"rus\":\"Белокуракинский район\"},{\"slug\":\"severodonetsk\",\"rus\":\"Северодонецкий район\"},{\"slug\":\"kreminskyi\",\"rus\":\"Кременской район\"},{\"slug\":\"popasnyanskyi\",\"rus\":\"Попаснянский район\"},{\"slug\":\"starobilskyi\",\"rus\":\"Старобельский район\"},{\"slug\":\"bilovodskyi\",\"rus\":\"Беловодский район\"},{\"slug\":\"markivskyi\",\"rus\":\"Марковский район\"},{\"slug\":\"milovskyi\",\"rus\":\"Меловской район\"},{\"slug\":\"novopskovskyi\",\"rus\":\"Новопсковский район\"},{\"slug\":\"shchastynskyi\",\"rus\":\"Щастинский район\"},{\"slug\":\"krasnodonsky\",\"rus\":\"Краснодонский район\"},{\"slug\":\"perevalsky\",\"rus\":\"Перевальский район\"},{\"slug\":\"stanichno-luganskiy\",\"rus\":\"Станично-Луганский район\"},{\"slug\":\"novoaidar\",\"rus\":\"Новоайдарский район\"}],\"lviv\":[{\"slug\":\"drohobychskyi\",\"rus\":\"Дрогобычский район\"},{\"slug\":\"zolochivskyi\",\"rus\":\"Золочевский район\"},{\"slug\":\"turkovsky\",\"rus\":\"Турковский район\"},{\"slug\":\"brody\",\"rus\":\"Бродовский район\"},{\"slug\":\"busk\",\"rus\":\"Бусский район\"},{\"slug\":\"lvivskyi\",\"rus\":\"Львовский район\"},{\"slug\":\"horodok\",\"rus\":\"Городокский район\"},{\"slug\":\"zhovkva\",\"rus\":\"Жолковский район\"},{\"slug\":\"kamianka-buzka\",\"rus\":\"Каменка-Бугский район\"},{\"slug\":\"peremyshliany\",\"rus\":\"Перемышлянский район\"},{\"slug\":\"pustomyty\",\"rus\":\"Пустомытовский район\"},{\"slug\":\"sambirskyi\",\"rus\":\"Самборский район\"},{\"slug\":\"starosambirskyi\",\"rus\":\"Старосамборский район\"},{\"slug\":\"stryiskyi\",\"rus\":\"Стрыйский район\"},{\"slug\":\"zhidachiv\",\"rus\":\"Жидачовский район\"},{\"slug\":\"mykolaiv\",\"rus\":\"Николаевский район\"},{\"slug\":\"skole\",\"rus\":\"Сколевский район\"},{\"slug\":\"radekhiv\",\"rus\":\"Радеховский район\"},{\"slug\":\"sokal\",\"rus\":\"Сокальский район\"},{\"slug\":\"yavorivskyi\",\"rus\":\"Яворовский район\"},{\"slug\":\"mostyska\",\"rus\":\"Мостисский район\"}],\"mykolaiv\":[{\"slug\":\"bashtanskyi\",\"rus\":\"Баштанский район\"},{\"slug\":\"berezneguvatskyi\",\"rus\":\"Березнеговатский район\"},{\"slug\":\"novobuzkyi\",\"rus\":\"Новобугский район\"},{\"slug\":\"snihurivskyi\",\"rus\":\"Снигирёвский район\"},{\"slug\":\"voznesenskyi\",\"rus\":\"Вознесенский район\"},{\"slug\":\"kazankovsky\",\"rus\":\"Казанковский район\"},{\"slug\":\"vitovsky\",\"rus\":\"Витовский район\"},{\"slug\":\"bratskyi\",\"rus\":\"Братский район\"},{\"slug\":\"veselynivskyi\",\"rus\":\"Веселиновский район\"},{\"slug\":\"domanivskyi\",\"rus\":\"Доманёвский район\"},{\"slug\":\"yelanetskyi\",\"rus\":\"Еланецкий район\"},{\"slug\":\"mykolaivskyi\",\"rus\":\"Николаевский район\"},{\"slug\":\"berezhanskyi\",\"rus\":\"Березанский район\"},{\"slug\":\"novoodeskyi\",\"rus\":\"Новоодесский район\"},{\"slug\":\"ochakivskyi\",\"rus\":\"Очаковский район\"},{\"slug\":\"perwomayskyi\",\"rus\":\"Первомайский район\"},{\"slug\":\"arbuzynskyi\",\"rus\":\"Арбузинский район\"},{\"slug\":\"vradiyivskyi\",\"rus\":\"Врадиевский район\"},{\"slug\":\"kryvoozerskyi\",\"rus\":\"Кривоозерский район\"}],\"odesa\":[{\"slug\":\"berezivskyi\",\"rus\":\"Березовский район\"},{\"slug\":\"mykolaivskyi\",\"rus\":\"Николаевский район\"},{\"slug\":\"shyriaiivskyi\",\"rus\":\"Ширяевский район\"},{\"slug\":\"ivanovsky\",\"rus\":\"Ивановский район\"},{\"slug\":\"bilhorod-dnistrovskyi\",\"rus\":\"Белгород-Днестровский район\"},{\"slug\":\"saratovskyi\",\"rus\":\"Саратский район\"},{\"slug\":\"tatarbunarskyi\",\"rus\":\"Татарбунарский район\"},{\"slug\":\"bolhradskyi\",\"rus\":\"Болградский район\"},{\"slug\":\"artsyzkyi\",\"rus\":\"Арцизский район\"},{\"slug\":\"tarutynskyi\",\"rus\":\"Тарутинский район\"},{\"slug\":\"izmailskyi\",\"rus\":\"Измаильский район\"},{\"slug\":\"kiliiskyi\",\"rus\":\"Килийский район\"},{\"slug\":\"reniyskyi\",\"rus\":\"Ренийский район\"},{\"slug\":\"biliaivskyi\",\"rus\":\"Беляевский район\"},{\"slug\":\"ovidiopolskyi\",\"rus\":\"Овидиопольский район\"},{\"slug\":\"kominternovsky\",\"rus\":\"Коминтерновский район\"},{\"slug\":\"ananiivskyi\",\"rus\":\"Ананьевский район\"},{\"slug\":\"baltskyi\",\"rus\":\"Балтский район\"},{\"slug\":\"kodymskyi\",\"rus\":\"Кодымский район\"},{\"slug\":\"liubashivskyi\",\"rus\":\"Любашёвский район\"},{\"slug\":\"kotovsky\",\"rus\":\"Котовский район\"},{\"slug\":\"krasnooknyansky\",\"rus\":\"Красноокнянский район\"},{\"slug\":\"savranskyi\",\"rus\":\"Савранский район\"},{\"slug\":\"rozdilnianskyi\",\"rus\":\"Раздельнянский район\"},{\"slug\":\"velykomikhaylivskyi\",\"rus\":\"Великомихайловский район\"},{\"slug\":\"frunzovsky\",\"rus\":\"Фрунзовский район\"}],\"poltava\":[{\"slug\":\"poltava\",\"rus\":\"Полтавский район\"},{\"slug\":\"dikanka\",\"rus\":\"Диканьский район\"},{\"slug\":\"zinkiv\",\"rus\":\"Зеньковский район\"},{\"slug\":\"karlivka\",\"rus\":\"Карловский район\"},{\"slug\":\"kobelyaki\",\"rus\":\"Кобелякский район\"},{\"slug\":\"kotelva\",\"rus\":\"Котелевский район\"},{\"slug\":\"mashevsky\",\"rus\":\"Машевский район\"},{\"slug\":\"chutovsky\",\"rus\":\"Чутовский район\"},{\"slug\":\"novi-sanzhary\",\"rus\":\"Новосанжарский район\"},{\"slug\":\"reshetylivka\",\"rus\":\"Решетиловский район\"},{\"slug\":\"kremenchuk\",\"rus\":\"Кременчугский район\"},{\"slug\":\"globino\",\"rus\":\"Глобинский район\"},{\"slug\":\"semenovsky\",\"rus\":\"Семёновский район\"},{\"slug\":\"kozelshchinsky\",\"rus\":\"Козельщинский район\"},{\"slug\":\"mirgorod\",\"rus\":\"Миргородский район\"},{\"slug\":\"gadyach\",\"rus\":\"Гадячский район\"},{\"slug\":\"velykobagachansky\",\"rus\":\"Великобагачанский район\"},{\"slug\":\"lokhvytsia\",\"rus\":\"Лохвицкий район\"},{\"slug\":\"shishaki\",\"rus\":\"Шишацкий район\"},{\"slug\":\"lubny\",\"rus\":\"Лубенский район\"},{\"slug\":\"hrebinka\",\"rus\":\"Гребенковский район\"},{\"slug\":\"orzhytsky\",\"rus\":\"Оржицкий район\"},{\"slug\":\"pyryatyn\",\"rus\":\"Пирятинский район\"},{\"slug\":\"khorol\",\"rus\":\"Хорольский район\"},{\"slug\":\"chornukhine\",\"rus\":\"Чернухинский район\"}],\"rivne\":[{\"slug\":\"volodymyrets\",\"rus\":\"Владимирецкий район\"},{\"slug\":\"zarichnenskyi\",\"rus\":\"Заречненский район\"},{\"slug\":\"dubenskyi\",\"rus\":\"Дубенский район\"},{\"slug\":\"demidivskyi\",\"rus\":\"Демидовский район\"},{\"slug\":\"mlynivskyi\",\"rus\":\"Млиновский район\"},{\"slug\":\"radivilivskyi\",\"rus\":\"Радивиловский район\"},{\"slug\":\"rivnenskyi\",\"rus\":\"Ровенский район\"},{\"slug\":\"bereznivskyi\",\"rus\":\"Березновский район\"},{\"slug\":\"hoshchanskyi\",\"rus\":\"Гощанский район\"},{\"slug\":\"zdolbunivskyi\",\"rus\":\"Здолбуновский район\"},{\"slug\":\"koretskyi\",\"rus\":\"Корецкий район\"},{\"slug\":\"kostopilskyi\",\"rus\":\"Костопольский район\"},{\"slug\":\"ostrozkyi\",\"rus\":\"Острожский район\"},{\"slug\":\"sarnenskyi\",\"rus\":\"Сарненский район\"},{\"slug\":\"dubrovitskyi\",\"rus\":\"Дубровицкий район\"},{\"slug\":\"rokytnivskyi\",\"rus\":\"Рокитновский район\"}],\"sumy\":[{\"slug\":\"konotopskyi\",\"rus\":\"Конотопский район\"},{\"slug\":\"burynskyi\",\"rus\":\"Бурынский район\"},{\"slug\":\"krolevetskyi\",\"rus\":\"Кролевецкий район\"},{\"slug\":\"putivlskyi\",\"rus\":\"Путивльский район\"},{\"slug\":\"okhtyrskyi\",\"rus\":\"Ахтырский район\"},{\"slug\":\"velykopysarivskyi\",\"rus\":\"Великописаревский район\"},{\"slug\":\"trostyanetskyi\",\"rus\":\"Тростянецкий район\"},{\"slug\":\"romenskyi\",\"rus\":\"Роменский район\"},{\"slug\":\"lypovodynskyi\",\"rus\":\"Липоводолинский район\"},{\"slug\":\"nedryhaylivskyi\",\"rus\":\"Недригайловский район\"},{\"slug\":\"sumskyi\",\"rus\":\"Сумской район\"},{\"slug\":\"bilopilskyi\",\"rus\":\"Белопольский район\"},{\"slug\":\"krasnopilskyi\",\"rus\":\"Краснопольский район\"},{\"slug\":\"lebedynskyi\",\"rus\":\"Лебединский район\"},{\"slug\":\"shostkinskyi\",\"rus\":\"Шосткинский район\"},{\"slug\":\"hlukhivskyi\",\"rus\":\"Глуховский район\"},{\"slug\":\"seredynobudskyi\",\"rus\":\"Середино-Будский район\"},{\"slug\":\"yampilskyi\",\"rus\":\"Ямпольский район\"}],\"ternopil\":[{\"slug\":\"kremenetskyy\",\"rus\":\"Кременецкий район\"},{\"slug\":\"lanovetskyy\",\"rus\":\"Лановецкий район\"},{\"slug\":\"shumskyy\",\"rus\":\"Шумский район\"},{\"slug\":\"ternopilskyy\",\"rus\":\"Тернопольский район\"},{\"slug\":\"berezhanskyy\",\"rus\":\"Бережанский район\"},{\"slug\":\"kozovsky\",\"rus\":\"Козовский район\"},{\"slug\":\"zbarazkyy\",\"rus\":\"Збаражский район\"},{\"slug\":\"zborivskyy\",\"rus\":\"Зборовский район\"},{\"slug\":\"pidvolochyskyy\",\"rus\":\"Подволочисский район\"},{\"slug\":\"pidhayetskyy\",\"rus\":\"Подгаецкий район\"},{\"slug\":\"terebovlyanskyy\",\"rus\":\"Теребовлянский район\"},{\"slug\":\"chortkivskyy\",\"rus\":\"Чортковский район\"},{\"slug\":\"borschivskyy\",\"rus\":\"Борщёвский район\"},{\"slug\":\"buchatskyy\",\"rus\":\"Бучачский район\"},{\"slug\":\"husyatynskyy\",\"rus\":\"Гусятинский район\"},{\"slug\":\"zalishchytskyy\",\"rus\":\"Залещицкий район\"},{\"slug\":\"monastyryskyy\",\"rus\":\"Монастырский район\"}],\"kharkiv\":[{\"slug\":\"bohodukhivskyy\",\"rus\":\"Богодуховский район\"},{\"slug\":\"valkivskyy\",\"rus\":\"Валковский район\"},{\"slug\":\"zolochivskyy\",\"rus\":\"Золочевский район\"},{\"slug\":\"kolomatskyy\",\"rus\":\"Коломакский район\"},{\"slug\":\"krasnokutskyy\",\"rus\":\"Краснокутский район\"},{\"slug\":\"izyumskyy\",\"rus\":\"Изюмский район\"},{\"slug\":\"balakliyskyy\",\"rus\":\"Балаклейский район\"},{\"slug\":\"barvinkivskyy\",\"rus\":\"Барвенковский район\"},{\"slug\":\"borivskyy\",\"rus\":\"Боровский район\"},{\"slug\":\"krasnogradskyy\",\"rus\":\"Красноградский район\"},{\"slug\":\"zachepylivskyy\",\"rus\":\"Зачепиловский район\"},{\"slug\":\"kehychivskyy\",\"rus\":\"Кегичевский район\"},{\"slug\":\"sakhnovshchynskyy\",\"rus\":\"Сахновщинский район\"},{\"slug\":\"kupyanskyy\",\"rus\":\"Купянский район\"},{\"slug\":\"velykoburlutska\",\"rus\":\"Великобурлукский район\"},{\"slug\":\"dvorichanskyy\",\"rus\":\"Двуречанский район\"},{\"slug\":\"shevchenkivskyy\",\"rus\":\"Шевченковский район\"},{\"slug\":\"lozivskyy\",\"rus\":\"Лозовский район\"},{\"slug\":\"blyznyukivskyy\",\"rus\":\"Близнюковский район\"},{\"slug\":\"permyakivskyy\",\"rus\":\"Первомайский район\"},{\"slug\":\"kharkivskyy\",\"rus\":\"Харьковский район\"},{\"slug\":\"derhachivskyy\",\"rus\":\"Дергачёвский район\"},{\"slug\":\"novovodolazkyy\",\"rus\":\"Нововодолажский район\"},{\"slug\":\"chuhuyivskyy\",\"rus\":\"Чугуевский район\"},{\"slug\":\"vovchanskyy\",\"rus\":\"Волчанский район\"},{\"slug\":\"zmiiivskyy\",\"rus\":\"Змиевской район\"},{\"slug\":\"pechenizkyy\",\"rus\":\"Печенежский район\"}],\"kherson\":[{\"slug\":\"berislavskyy\",\"rus\":\"Бериславский район\"},{\"slug\":\"velykookseandrivskyy\",\"rus\":\"Великоалександровский район\"},{\"slug\":\"vysokopilskyy\",\"rus\":\"Высокопольский район\"},{\"slug\":\"novovorontsovskyy\",\"rus\":\"Нововоронцовский район\"},{\"slug\":\"henicheskyy\",\"rus\":\"Генический район\"},{\"slug\":\"ivanivskyy\",\"rus\":\"Ивановский район\"},{\"slug\":\"nizhnosirohozskyy\",\"rus\":\"Нижнесерогозский район\"},{\"slug\":\"novotroitskyy\",\"rus\":\"Новотроицкий район\"},{\"slug\":\"kakhovskyy\",\"rus\":\"Каховский район\"},{\"slug\":\"velykolepetyskyy\",\"rus\":\"Великолепетихский район\"},{\"slug\":\"verkhnyorohachytskyy\",\"rus\":\"Верхнерогачикский район\"},{\"slug\":\"hornostayivskyy\",\"rus\":\"Горностаевский район\"},{\"slug\":\"chaplynskyy\",\"rus\":\"Чаплинский район\"},{\"slug\":\"skadovskyy\",\"rus\":\"Скадовский район\"},{\"slug\":\"holoprystanskyy\",\"rus\":\"Голопристанский район\"},{\"slug\":\"kalanchatskyy\",\"rus\":\"Каланчакский район\"},{\"slug\":\"bilozeretskyy\",\"rus\":\"Белозёрский район\"},{\"slug\":\"oleshkivskyy\",\"rus\":\"Олешковский район\"}],\"khmelnytskyi\":[{\"slug\":\"kamyanets-podilskyy\",\"rus\":\"Каменец-Подольский район\"},{\"slug\":\"dunayevetskyy\",\"rus\":\"Дунаевецкий район\"},{\"slug\":\"novoushytskyy\",\"rus\":\"Новоушицкий район\"},{\"slug\":\"chemerovetskyy\",\"rus\":\"Чемеровецкий район\"},{\"slug\":\"khmelnytskyy\",\"rus\":\"Хмельницкий район\"},{\"slug\":\"vinkovetskyy\",\"rus\":\"Виньковецкий район\"},{\"slug\":\"volochyskyy\",\"rus\":\"Волочисский район\"},{\"slug\":\"horodotskyy\",\"rus\":\"Городокский район\"},{\"slug\":\"derazhnyanskyy\",\"rus\":\"Деражнянский район\"},{\"slug\":\"krasylivskyy\",\"rus\":\"Красиловский район\"},{\"slug\":\"letychivskyy\",\"rus\":\"Летичевский район\"},{\"slug\":\"starokostyantynivskyy\",\"rus\":\"Староконстантиновский район\"},{\"slug\":\"starosyniavskyy\",\"rus\":\"Старосинявский район\"},{\"slug\":\"teofipolksyy\",\"rus\":\"Теофипольский район\"},{\"slug\":\"yarmolyntsiyskyy\",\"rus\":\"Ярмолинецкий район\"},{\"slug\":\"shepetivskyy\",\"rus\":\"Шепетовский район\"},{\"slug\":\"bilohirskyy\",\"rus\":\"Белогорский район\"},{\"slug\":\"polonsky\",\"rus\":\"Полонский район\"},{\"slug\":\"izyaslavskyy\",\"rus\":\"Изяславский район\"},{\"slug\":\"slavutskyy\",\"rus\":\"Славутский район\"}],\"cherkasy\":[{\"slug\":\"zvenyhorodskyy\",\"rus\":\"Звенигородский район\"},{\"slug\":\"katerynopilskyy\",\"rus\":\"Катеринопольский район\"},{\"slug\":\"lysianskyy\",\"rus\":\"Лысянский район\"},{\"slug\":\"talnivskyy\",\"rus\":\"Тальновский район\"},{\"slug\":\"shpolyanskyy\",\"rus\":\"Шполянский район\"},{\"slug\":\"zolotonoshskyy\",\"rus\":\"Золотоношский район\"},{\"slug\":\"drabivskyy\",\"rus\":\"Драбовский район\"},{\"slug\":\"chornobayivskyy\",\"rus\":\"Чернобаевский район\"},{\"slug\":\"umanskyy\",\"rus\":\"Уманский район\"},{\"slug\":\"zhashkivskyy\",\"rus\":\"Жашковский район\"},{\"slug\":\"mankivskyy\",\"rus\":\"Маньковский район\"},{\"slug\":\"monastyryshchenskyy\",\"rus\":\"Монастырищенский район\"},{\"slug\":\"khrystynivskyy\",\"rus\":\"Христиновский район\"},{\"slug\":\"cherkaskyy\",\"rus\":\"Черкасский район\"},{\"slug\":\"horodyshchenskyy\",\"rus\":\"Городищенский район\"},{\"slug\":\"kamyanetskyy\",\"rus\":\"Каменский район\"},{\"slug\":\"kanivskyy\",\"rus\":\"Каневский район\"},{\"slug\":\"korsun-shevchenkivskyy\",\"rus\":\"Корсунь-Шевченковский район\"},{\"slug\":\"smilyanskyy\",\"rus\":\"Смелянский район\"},{\"slug\":\"chyhyrynskyy\",\"rus\":\"Чигиринский район\"}],\"chernihiv\":[{\"slug\":\"koriukivskyy\",\"rus\":\"Корюковский район\"},{\"slug\":\"menskyy\",\"rus\":\"Менский район\"},{\"slug\":\"snovskyy\",\"rus\":\"Сновский район\"},{\"slug\":\"sosnytskyy\",\"rus\":\"Сосницкий район\"},{\"slug\":\"nizhynskyy\",\"rus\":\"Нежинский район\"},{\"slug\":\"bakhmatskyy\",\"rus\":\"Бахмачский район\"},{\"slug\":\"bobrovytskyy\",\"rus\":\"Бобровицкий район\"},{\"slug\":\"borznyanskyy\",\"rus\":\"Борзнянский район\"},{\"slug\":\"nosivskyy\",\"rus\":\"Носовский район\"},{\"slug\":\"novhorod-siverskyy\",\"rus\":\"Новгород-Северский район\"},{\"slug\":\"koropskyy\",\"rus\":\"Коропский район\"},{\"slug\":\"semenivskyy\",\"rus\":\"Семёновский район\"},{\"slug\":\"prylutskyy\",\"rus\":\"Прилукский район\"},{\"slug\":\"varvynskyy\",\"rus\":\"Варвинский район\"},{\"slug\":\"ichnyanskyy\",\"rus\":\"Ичнянский район\"},{\"slug\":\"sribnyanskyy\",\"rus\":\"Сребнянский район\"},{\"slug\":\"talalaivskyy\",\"rus\":\"Талалаевский район\"},{\"slug\":\"chernihivskyy\",\"rus\":\"Черниговский район\"},{\"slug\":\"horodnyanskyy\",\"rus\":\"Городнянский район\"},{\"slug\":\"kozeletskyy\",\"rus\":\"Козелецкий район\"},{\"slug\":\"kulikivskyy\",\"rus\":\"Куликовский район\"},{\"slug\":\"ripkynskyy\",\"rus\":\"Репкинский район\"}],\"chernivtsi\":[{\"slug\":\"vyzhnytskyy\",\"rus\":\"Вижницкий район\"},{\"slug\":\"putylskyy\",\"rus\":\"Путильский район\"},{\"slug\":\"kelmenetsky\",\"rus\":\"Кельменецкий район\"},{\"slug\":\"sokyryanskyy\",\"rus\":\"Сокирянский район\"},{\"slug\":\"khotynskyy\",\"rus\":\"Хотинский район\"},{\"slug\":\"herchayivskyy\",\"rus\":\"Герцаевский район\"},{\"slug\":\"hlybotskyy\",\"rus\":\"Глыбокский район\"},{\"slug\":\"zastavnyvskyy\",\"rus\":\"Заставновский район\"},{\"slug\":\"kitsmanskyy\",\"rus\":\"Кицманский район\"},{\"slug\":\"novoselytskyy\",\"rus\":\"Новоселицкий район\"},{\"slug\":\"storozhynetskyy\",\"rus\":\"Сторожинецкий район\"}],\"krym\":[{\"slug\":\"bakhchisarayskyy\",\"rus\":\"Бахчисарайский район\"},{\"slug\":\"bilohirskyy\",\"rus\":\"Белогорский район\"},{\"slug\":\"nizhnogirskyy\",\"rus\":\"Нижнегорский район\"},{\"slug\":\"djankoy\",\"rus\":\"Джанкойский район\"},{\"slug\":\"yevpatoriyskyy\",\"rus\":\"Евпатория\"},{\"slug\":\"chornomorskyy\",\"rus\":\"Черноморский район\"},{\"slug\":\"sakskyy\",\"rus\":\"Сакский район\"},{\"slug\":\"kerchenskyy\",\"rus\":\"Керченский район\"},{\"slug\":\"kurmanskyy\",\"rus\":\"Курманский район\"},{\"slug\":\"pervomayskyy\",\"rus\":\"Первомайский район\"},{\"slug\":\"perekopskyy\",\"rus\":\"Перекопский район\"},{\"slug\":\"rozdolnenskyy\",\"rus\":\"Раздольненский район\"},{\"slug\":\"simferopolskyy\",\"rus\":\"Симферопольский район\"},{\"slug\":\"sudak\",\"rus\":\"Судакский район\"},{\"slug\":\"feodosiya\",\"rus\":\"Феодосийский район\"},{\"slug\":\"yaltyntskyy\",\"rus\":\"Ялтинский район\"},{\"slug\":\"alushta\",\"rus\":\"Алуштинский район\"}]}";
        ObjectMapper mapper = new ObjectMapper();
        ourOblRegHashmap = mapper.readValue(json, new TypeReference<HashMap<String, List<Region>>>(){});
        ourOblRegHashmap.forEach((key, value) -> System.out.println(key + " " + value.size()));
    }
    public void start() throws IOException {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.idcompass.com/?lang=ru&section=base");
        Cookie cookie = new Cookie("PHPSESSID", "ab333baffbb0c9c580e5602a2dcfdba3");
        driver.manage().addCookie(cookie);
        for (int i=0; i<25; i++) {
            WebElement el = driver.findElements(By.cssSelector("#regionsList .regionsTable tbody tr a")).get(i);
            String rez = el.getDomAttribute("href");
            int charPos = rez.lastIndexOf("=");
            String obl = rez.substring(charPos + 1);
            System.out.println("********************************************* oblast " + obl);
            el.click();
            this.getOblastsToHashMap();
            String oblToSend = ourOblToOblHashMap.get(obl);
//            parseRegion(driver.getPageSource(), oblToSend);
            List<WebElement> regionsElements = driver.findElements(By.cssSelector(".district a"));
            for (int j=0; j<regionsElements.size(); j++) {
                WebElement regClick = driver.findElements(By.cssSelector(".district a")).get(j);
                String reg = regClick.getText();
                if (obl.equals("volyn") && reg.equals("Любешовский район")) reg = "Любешевский район";
                if (obl.equals("vinnytsia") && reg.equals("Тепликский район")) reg = "Теплицкий район";
                if (obl.equals("vinnytsia") && reg.equals("Черневецкий район")) reg = "Черновицкий район";
                if (obl.equals("zhytomyr") && reg.equals("Пулинський район")) reg = "Пулинский район";
//                if (obl.equals("chernivtsi") && reg.equals("Кельменецкий район")) reg = "Днестровский район";
//                if (obl.equals("khmelnytskyi") && reg.equals("Полонский район")) reg = "Шепетовский район";
//                if (obl.equals("ternopil") && reg.equals("Козовский район")) reg = "Тернопольский район";
//                if (obl.equals("poltava") && reg.equals("Великобагачанский район")) reg = "Миргородский район";
//                if (obl.equals("poltava") && reg.equals("Козельщинский район")) reg = "Кременчугский район";
//                if (obl.equals("poltava") && reg.equals("Машевский район")) reg = "Полтавский район";
//                if (obl.equals("poltava") && reg.equals("Оржицкий район")) reg = "Лубенский район";
//                if (obl.equals("poltava") && reg.equals("Семёновский район")) reg = "Кременчугский район";
//                if (obl.equals("poltava") && reg.equals("Чутовский район")) reg = "Полтавский район";
//                if (obl.equals("odessa") && reg.equals("Ивановский район")) reg = "Березовский район";
//                if (obl.equals("odessa") && reg.equals("Коминтерновский район")) reg = "Лиманский район";
//                if (obl.equals("odessa") && reg.equals("Котовский район")) reg = "Подольский район";
//                if (obl.equals("odessa") && reg.equals("Красноокнянский район")) reg = "Окнянский район";
//                if (obl.equals("odessa") && reg.equals("Фрунзовский район")) reg = "Захарьевский район";
//                if (obl.equals("mykolaiv") && reg.equals("Витовский район")) reg = "Николаевский район";
//                if (obl.equals("mykolaiv") && reg.equals("Казанковский район")) reg = "Баштанский район";
//                if (obl.equals("lviv") && reg.equals("Турковский район")) reg = "Самборский район";
                if (obl.equals("lviv") && reg.equals("Львов")) reg = "Львовский район";
//                if (obl.equals("lviv") && reg.equals("Старосамборский район")) reg = "Самборский район";
//                if (obl.equals("luhansk") && reg.equals("Краснодонский район")) reg = "Луганский район";
//                if (obl.equals("luhansk") && reg.equals("Меловской район")) reg = "Луганский район";
//                if (obl.equals("luhansk") && reg.equals("Перевальский район")) reg = "Луганский район";
                if (obl.equals("luhansk") && reg.equals("Сєвєродонецький район")) reg = "Северодонецкий район";
                if (obl.equals("luhansk") && reg.equals("Свердловский район")) reg = "Луганский район";
                if (obl.equals("luhansk") && reg.equals("Славяносербский район")) reg = "Луганский район";
                if (obl.equals("luhansk") && reg.equals("Троицкий район")) reg = "Луганский район";
                if (obl.equals("luhansk") && reg.equals("Луганск")) reg = "Луганский район";
                if (obl.equals("kirovohrad") && reg.equals("Кировоградский район")) reg = "Кропивницкий район";
//                if (obl.equals("kirovohrad") && reg.equals("Устиновский район")) reg = "Кропивницкий район";
                if (obl.equals("kirovohrad") && reg.equals("Александровский район")) reg = "Александрийский район";
//                if (obl.equals("kiev") && reg.equals("Тетиевский район")) reg = "Белоцерковский район";
//                if (obl.equals("kiev") && reg.equals("Киево-Святошинский район")) reg = "Фастовский район";
//                if (obl.equals("kiev") && reg.equals("Переяслав-Хмельницкий район")) reg = "Переяславский район";
//                if (obl.equals("zaporizhia") && reg.equals("Великобелозёрский район")) reg = "Васильевский район";
//                if (obl.equals("zaporizhia") && reg.equals("Запорожье")) reg = "Запорожский район";
//                if (obl.equals("donetsk") && reg.equals("Першотравневый район")) reg = "Мангушский район";
//                if (obl.equals("donetsk") && reg.equals("Тельмановский район")) reg = "Кальмиусский район";
                if (obl.equals("donetsk") && reg.equals("Бахму́тский район")) reg = "Бахмутский район";
                if (obl.equals("donetsk") && reg.equals("Шахтёрский район")) reg = "Шахтерский район";
//                if (obl.equals("dnipropetrovsk") && reg.equals("Межевский район")) reg = "Синельниковский район";
//                if (obl.equals("dnipropetrovsk") && reg.equals("Солонянский район")) reg = "Днепровский район";
                if (obl.equals("dnipropetrovsk") && reg.equals("Днепропетровский район")) reg = "Днепровский район";
                if (obl.equals("kherson") && reg.equals("Голая Пристань")) reg = "Голопристанский район";
                if (obl.equals("kherson") && reg.equals("Олешківський район")) reg = "Олешковский район";
                if (obl.equals("kherson") && reg.equals("Чаплинский район - ДЕМО-ВЕРСИЯ")) reg = "Чаплинский район";
                if (obl.equals("crimea") && reg.equals("Алушта")) reg = "Алуштинский район";
                if (obl.equals("crimea") && reg.equals("Армянск")) reg = "Перекопский район";
                if (obl.equals("crimea") && reg.equals("Джанкой")) reg = "Джанкойский район";
                if (obl.equals("crimea") && reg.equals("Саки")) reg = "Сакский район";
                if (obl.equals("crimea") && reg.equals("Судак")) reg = "Судакский район";
                if (obl.equals("crimea") && reg.equals("Ялта")) reg = "Ялтинский район";
                if (obl.equals("crimea") && reg.equals("Феодосия")) reg = "Феодосийский район";
                if (obl.equals("crimea") && reg.equals("Симферополь")) reg = "Симферопольский район";
                if (obl.equals("crimea") && reg.equals("Кировский район")) reg = "Феодосийский район";
                if (obl.equals("crimea") && reg.equals("Красногвардейский район")) reg = "Курманский район";
                if (obl.equals("crimea") && reg.equals("Красноперекопск")) reg = "Перекопский район";
                if (obl.equals("crimea") && reg.equals("Красноперекопский район")) reg = "Перекопский район";
                if (obl.equals("crimea") && reg.equals("Ленинский район")) reg = "Керченский район";
                if (obl.equals("crimea") && reg.equals("Советский район")) reg = "Феодосийский район";
                String regToSend = "";
                for (Map.Entry<String, List<Region>> entry : ourOblRegHashmap.entrySet()){
                    if (Objects.equals(oblToOurOblHashMap.get(entry.getKey()), obl)) {
                        for (Region region : entry.getValue()){
//                            String subRegMy = region.getRus().substring(0, 3);
                            if (Objects.equals(region.getRus().trim(), reg.trim())
//                                    || reg.startsWith(subRegMy)
                            ) {
//                                for (Region r : ourOblRegHashmap.get(oblToSend)) {
//                                    if (r.getRus().startsWith(subRegMy)) {
                                        regToSend = region.getSlug();
//                                    }
//                                }
                            }
                        }
                    }
                }
                WebDriverWait wait= (new WebDriverWait(driver, Duration.ofSeconds(20)));
                wait.until(ExpectedConditions.elementToBeClickable(regClick));
                regClick.click();
                parseRegion(driver.getPageSource(), oblToSend, regToSend);
                driver.navigate().back();
            }
            driver.navigate().back();
        }
        driver.quit();
    }
    public void getOblastsToHashMap() {
        try {
            String filePath = "https://www.idcompass.com/?lang=ru&section=base";
            Document doc = Jsoup.connect(filePath).cookie("PHPSESSID", "4342526be1ebcafea7ff111dc420119a").get();
            Elements paragraphs = doc.select("#regionsList .regionsTable tbody tr");
            for (Element paragraph : paragraphs) {
                String rez = paragraph.select("a").attr("href");
                int charPos = rez.lastIndexOf("=");
                String substringAfterLastChar = rez.substring(charPos + 1);
                if (substringAfterLastChar.equals("ivanofrankivsk")){
                    oblToOurOblHashMap.put("ivano-frankivsk", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "ivano-frankivsk");
                } else if (substringAfterLastChar.equals("kiev")) {
                    oblToOurOblHashMap.put("kyiv", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "kyiv");
                } else if (substringAfterLastChar.equals("vinnytsia")) {
                    oblToOurOblHashMap.put("vinnycya", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "vinnycya");
                } else if (substringAfterLastChar.equals("crimea")) {
                    oblToOurOblHashMap.put("krym", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "krym");
                } else if (substringAfterLastChar.equals("dnipropetrovsk")) {
                    oblToOurOblHashMap.put("dnipro", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "dnipro");
                } else if (substringAfterLastChar.equals("zakarpattia")) {
                    oblToOurOblHashMap.put("zakarpattya", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "zakarpattya");
                } else if (substringAfterLastChar.equals("zaporizhia")) {
                    oblToOurOblHashMap.put("zaporizhzhya", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "zaporizhzhya");
                } else if (substringAfterLastChar.equals("kirovohrad")) {
                    oblToOurOblHashMap.put("kropyvnytskyi", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "kropyvnytskyi");
                } else if (substringAfterLastChar.equals("luhansk")) {
                    oblToOurOblHashMap.put("lugansk", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "lugansk");
                } else if (substringAfterLastChar.equals("odessa")) {
                    oblToOurOblHashMap.put("odesa", substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, "odesa");
                } else {
                    oblToOurOblHashMap.put(substringAfterLastChar, substringAfterLastChar);
                    ourOblToOblHashMap.put(substringAfterLastChar, substringAfterLastChar);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void parseRegion(String region, String obl, String reg) throws IOException {
        Document doc = Jsoup.parse(region);
        Elements paragraphs = doc.select("#pageContent #blocksContent .miniblock");
        for (Element paragraph : paragraphs) {
            if (!paragraph.hasClass("yellow")){
                Agrarian agrarian = new Agrarian();
                List<String> phones = new ArrayList<>();
                List<String> emails = new ArrayList<>();
                Set<SellType> sellsArray = new HashSet<>();
                agrarian.setPhones(phones);
                agrarian.setEmails(emails);
                agrarian.setSells(sellsArray);
                agrarian.setOblast(obl);
                agrarian.setOldRegion(reg);

                Element imageElement = paragraph.select(".miniPic img").first();
                String imageUrl = imageElement.attr("src");
//				imageUrl = imageUrl.replaceAll(" ", "");
                if (!imageUrl.equals("")) {
                    if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                        imageUrl = "https://www.idcompass.com" + imageUrl;
                        Photo photo = downloadImage(imageUrl);
                        agrarian.setImage(photo);
                    }
                }
                agrarian.setTitle(paragraph.select("h3").text());
                agrarian.setAddress(paragraph.select("p").get(0).text());
                agrarian.setHead(paragraph.select("p").get(1).getElementsByIndexEquals(1).text());
                boolean f = false;
                String sells2 = paragraph.select("p").get(2).getElementsByIndexEquals(1).text();
                String sells1 = sells2.replaceAll("\\*", "");
                String sells = sells1.replaceAll("\\?", ",");
                Pattern pSells = Pattern.compile("(?<=^|,|\\.|;|/)([^,;/.\\(]*(\\([^\\)]*\\))?[^,.;/\\(]*)*");
                Matcher mSells = pSells.matcher(sells);
                while (mSells.find()) {
                    String resS= "";
                    if (!mSells.group().trim().isEmpty()) {
                        resS =mSells.group().trim().toLowerCase();
                    } else {
                        resS = mSells.group().toLowerCase();
                    }
                    if (resS.isEmpty()) continue;
//                    checkEnumType(resS);
                    Pattern itemSellsPattern1 = Pattern.compile("Рослинництво.*|рослинництв.*|растени.*|растериеводство|росл.*|хвоя|хвой.*|.*насінн.*|.*ландшафт.*|.*семен.*|семечки|сорго|.*ліан.*|.*лиан.*|киви|теплиц.*|тепличн.*|.*декоратив.*|.*декоритив.*|.*коренепл.*|.*корнепл.*|конопля|скловолокно|.*однорічн.*|.*дворічн.*|.*багаторічн.*|кореандр|коріандр|кориандр|посівн.* матеріал.*|насыння|.*лікарськ.*|.*трави.*|сосна.*|дуб|акація|ківі|.*отруби.*|післяурожайна.*|.*землеробство|.*вирщування с/г культур.*|швидкорастучі та якісні породи дерев", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher1 = itemSellsPattern1.matcher(resS);
                    Pattern itemSellsPattern2 = Pattern.compile(".*зерн.*|.*пшен.*|.*пшон.*|.*жит.*|жыт|ячм.*|греч.*|овес|просо|рожь|куку.*|кукру.*|бобов.*|бобві|боби|квасоля|соя.*|сої|сою|чечевица|сочевиц.*|горох|зенові|елеватор|рис|злак.*|висів.*|.*озим.*|ярих|маис|маїс|тритикале|фасоль|вівса", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher2 = itemSellsPattern2.matcher(resS);
                    Pattern itemSellsPattern3 = Pattern.compile("технічн.*|технически.*|комбікорми|корм.*|жмих|цукровий буряк|цукрові буряки|цукровий завод|.*олійн.*|.*масляничн.*|.*маслянічн.*|.*маслич.*|семечка|подсолн.*|.*сонячни.*|.*соняшни.*|рицина|рипак|рапс|ріпак|суріпа|рижій|гірчи.*|.*горчи.*|мак|кунжут|арахіс|перила|лялеманція|сафлор|ефіроолійн.*|коріандр|кмин|м'ята|шавлія|лаванда|фенхель|аніс|прядивні|льон|лен|коноплі|бавовник|силос|сіно|сено|солома|люцерн.*|еспарцет|.*рижій.*|.*рижію.*|суданка", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher3 = itemSellsPattern3.matcher(resS);
                    Pattern itemSellsPattern4 = Pattern.compile("кормові|макух.*|жмих.*|жмых.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher4 = itemSellsPattern4.matcher(resS);
                    Pattern itemSellsPattern5 = Pattern.compile("овоч.*|овощ.*|томат.*|.*помідо.*|помидор.*|карто.*|капус.*|свекл.*|морк.*|огур.*|огір.*|цибул.*|буряк.*|гриб.*|печериці|кабач.*|репа|баклажан.*|лук.*|зелень.*|укроп|петрушка|кинза|шпинат|щавель|руккола|салат|мята|базилик|.*бульбоплод.*|.*перець.*|селера|редиска|картапля|часник|чеснок|редис.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher5 = itemSellsPattern5.matcher(resS);
                    Pattern itemSellsPattern6 = Pattern.compile("кавун.*|диня|дині|дыня|.*арбуз.*|баштан.*|.*бахч.*|тыкв.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher6 = itemSellsPattern6.matcher(resS);
                    Pattern itemSellsPattern7 = Pattern.compile("сад.*|ябл.*|фрукт.*|ягод.*|.*ягід.*|клубни.*|.*полуни.*|лохин.*|поричка.*|ожина|журавлина|чорниця|горобина|ежевика|алича|.*обліп.*|суниц.*|малин.*|смород.*|хмель|хміль|саженцы|кісточк.*|.*косточков.*|плодов.*|плоди|вишн.*|груш.*|земляни.*|сливы|сливу|сливи|.*слива|.*черешн.*|плодівниц.*|.*горіх.*|плоды.*|абрикос|персик|цитрусових|сажанці|кустарники|питомник|аґрус|жимолость|орехи", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher7 = itemSellsPattern7.matcher(resS);
                    Pattern itemSellsPattern8 = Pattern.compile("виноград.*|виниград", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher8 = itemSellsPattern8.matcher(resS);
                    Pattern itemSellsPattern9 = Pattern.compile("квіт.*|квытыв|рози|розы|цветы|троянди", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher9 = itemSellsPattern9.matcher(resS);
                    Pattern itemSellsPattern10 = Pattern.compile(".*твар.*|тванинництво|животнов.*|зоопарк.*|мисливство|шерсть|кози|кіз|кроли.*|.*сперм.*|.*крол.*|.*равлик.*|ферма|.*страус.*|звіроловство|.*вовн.*");
                    Matcher itemSellsMatcher10 = itemSellsPattern10.matcher(resS);
                    Pattern itemSellsPattern11 = Pattern.compile("скот.*|коров.*|коні|.*коне.*|телят.*|худоб.*|ВРХ|врх|крх|крс|КРС|буйволи|кінний туризм|пасовища|конярство|кіннозаводство|продукція конярства", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher11 = itemSellsPattern11.matcher(resS);
                    Pattern itemSellsPattern12 = Pattern.compile("свин.*|.*поросят.*|хряки|ландрас.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher12 = itemSellsPattern12.matcher(resS);
                    Pattern itemSellsPattern13 = Pattern.compile("вівц.*|вівчарств.*|овц.*|баран.*|овец.*|овеч.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher13 = itemSellsPattern13.matcher(resS);
                    Pattern itemSellsPattern14 = Pattern.compile(".*птиця|птиц.*|пттиця|птах.*|перепілк.*|.*перепеляч.*|куре.*|курк.*|курч.*|.*яйц.*|яєць|інкуба.*|инкубац.*|бролери|бройл.*|уток|гус.*|.*качки.*|.*качок.*|каченят|качині|кури|куря.*|.*пташинниц.*|циплята|звірі", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher14 = itemSellsPattern14.matcher(resS);
//                    Pattern itemSellsPattern15 = Pattern.compile("ліс.*|Ліс.*");
//                    Matcher itemSellsMatcher15 = itemSellsPattern15.matcher(resS);
                    Pattern itemSellsPattern16 = Pattern.compile("бдж.*|мед|пчолосім.*|.*пасіка.*|пчело.*|.*вуликів.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher16 = itemSellsPattern16.matcher(resS);
                    Pattern itemSellsPattern17 = Pattern.compile(".*харч.*|.*олія.*|.*олії.*|олію|.*круп.*|.*кулинарный.*|соки|.*вода.*|.*воду.*|.*води.*|квас|.*напо.*|чай|жири|оцет|.*пищевое.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher17 = itemSellsPattern17.matcher(resS);
                    Pattern itemSellsPattern18 = Pattern.compile("м'яс.*|мяс.*|м’яс.*|м\"яс.*|.*м‘яса.*|ковбас.*|ялович.*|ліверн.*|сосиски|сардельки|м'ясн.*|говядина|копченос.*|бекон", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher18 = itemSellsPattern18.matcher(resS);
                    Pattern itemSellsPattern19 = Pattern.compile(".*риб.*|.*рыб.*|.*ставкове господарство|білого амура|товстолоба|судака|сома|щуки|осетер.*|білуга|стерлядь|севрюга|.*аквакультура.*|морепродукти|.*ракопод.*|.*молюск.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher19 = itemSellsPattern19.matcher(resS);
                    Pattern itemSellsPattern20 = Pattern.compile(".*борошн.*|мука|муку", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher20 = itemSellsPattern20.matcher(resS);
                    Pattern itemSellsPattern21 = Pattern.compile("цукор|сахар|.*цукру.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher21 = itemSellsPattern21.matcher(resS);
                    Pattern itemSellsPattern22 = Pattern.compile("молок.*|молоч.*|.*сири|сир|бринза|вершки|кефір|кумис.*|кумыс", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher22 = itemSellsPattern22.matcher(resS);
                    Pattern itemSellsPattern23 = Pattern.compile("хліб.*|хлеб.*|пекарня", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher23 = itemSellsPattern23.matcher(resS);
                    Pattern itemSellsPattern24 = Pattern.compile("масло|масла", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher24 = itemSellsPattern24.matcher(resS);
                    Pattern itemSellsPattern25 = Pattern.compile("кондитер.*|торт.*|тістеч.*|печив.*|булоч.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher25 = itemSellsPattern25.matcher(resS);
                    Pattern itemSellsPattern26 = Pattern.compile("спирт.*|.*алкогольн.*|коняк|бренді", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher26 = itemSellsPattern26.matcher(resS);
                    Pattern itemSellsPattern27 = Pattern.compile("макаро.*|бакалія", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher27 = itemSellsPattern27.matcher(resS);
                    Pattern itemSellsPattern28 = Pattern.compile("пиво", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher28 = itemSellsPattern28.matcher(resS);
                    Pattern itemSellsPattern29 = Pattern.compile("вино|вина|винпродукція", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher29 = itemSellsPattern29.matcher(resS);
                    Pattern itemSellsPattern30 = Pattern.compile("крупи|крупы", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher30 = itemSellsPattern30.matcher(resS);
                    Pattern itemSellsPattern31 = Pattern.compile("консер.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher31 = itemSellsPattern31.matcher(resS);
                    Pattern itemSellsPattern32 = Pattern.compile("тютю.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher32 = itemSellsPattern32.matcher(resS);
                    Pattern itemSellsPattern33 = Pattern.compile("ліс.*|лес|дрова|пиломатеріали|дерево|.*древесина.*|.*столяр.*|.*деревин.*|.*дошка.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher33 = itemSellsPattern33.matcher(resS);
                    Pattern itemSellsPattern34 = Pattern.compile("змішане сільське господарство|.*сільськогоспод.*|.*выращивания сельскохозяйственных культур|.*виробництво, переробка та реалізація с/г продукції.*|продукція змішаного сільського господар-ства|змішане сільське господар-ство", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher34 = itemSellsPattern34.matcher(resS);
                    Pattern itemSellsPattern35 = Pattern.compile(".*будів.*|.*кірпіч.*|.*пісок.*|.*піск.*|.*граві.*|глину.*|глина.*|.*каолін.*|.*цегл.*|.*строит.*|.*строительн.*|.*підлогу, матеріали покриття.*|.*Плити труби іншіі вироби із пластмаси.*|.*мебе.*|.*мебл.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher35 = itemSellsPattern35.matcher(resS);
                    Pattern itemSellsPattern36 = Pattern.compile(".*нафт.*|.*паливо.*|.*пальним.*|.*вугіл.*|.*торговля топливом.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher36 = itemSellsPattern36.matcher(resS);
                    Pattern itemSellsPattern37 = Pattern.compile(".*засоби.* захисту.*|.*хімі.*|.*добрива.*|.*хімпрод.*|.*удобрени.*|.*сзр.*|.*хім продукти.*|трихограму", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher37 = itemSellsPattern37.matcher(resS);
                    Pattern itemSellsPattern38 = Pattern.compile(".*інша сфера.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher38 = itemSellsPattern38.matcher(resS);
                    Pattern itemSellsPattern39 = Pattern.compile(".*транспорт.*|.*перевез.*|подшипники|сальник|ремни|смазки|звездочки|.*машинами.*|.*мащини.*|.*автомоб.*|навантажувачі|.*технік.*|.*трактор.*|.*комбайн.*|.*запчаст.*|сельхоз технику|.*Аренда и лизинг сельскохозяйственной техники и оборудования.*|.*с/х техника.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher39 = itemSellsPattern39.matcher(resS);

                    if (itemSellsMatcher1.find()){
                        agrarian.getSells().add(SellType.ROSLYNNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher2.find()){
                        agrarian.getSells().add(SellType.ZERNOVI);
                        f = true;
                    }
                    if (itemSellsMatcher3.find()){
                        agrarian.getSells().add(SellType.TEKHNICHNI);
                        f = true;
                    }
                    if (itemSellsMatcher4.find()){
                        agrarian.getSells().add(SellType.KORMOVI);
                        f = true;
                    }
                    if (itemSellsMatcher5.find()){
                        agrarian.getSells().add(SellType.OVOCHEVI);
                        f = true;
                    }
                    if (itemSellsMatcher6.find()){
                        agrarian.getSells().add(SellType.BASHTANNI);
                        f = true;
                    }

                    if (itemSellsMatcher7.find()){
                        agrarian.getSells().add(SellType.SADIVNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher8.find()){
                        agrarian.getSells().add(SellType.VYNOGRADARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher9.find()){
                        agrarian.getSells().add(SellType.KVITKARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher10.find()){
                        agrarian.getSells().add(SellType.TVARYNNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher11.find()){
                        agrarian.getSells().add(SellType.SKOTARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher12.find()){
                        agrarian.getSells().add(SellType.SVYNARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher13.find()){
                        agrarian.getSells().add(SellType.VIVCHARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher14.find()){
                        agrarian.getSells().add(SellType.PTAKHIVNYTSTVO);
                        f = true;
                    }
//                    if (itemSellsMatcher15.find()){
//                        agrarian.getSells().add(SellType.RYBNYTSTVO);
//                        f = true;
//                    }
                    if (itemSellsMatcher16.find()){
                        agrarian.getSells().add(SellType.BJILNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher17.find()){
                        agrarian.getSells().add(SellType.HARCHOVA_PROMYSLOVIST);
                        f = true;
                    }
                    if (itemSellsMatcher18.find()){
                        agrarian.getSells().add(SellType.MYASO);
                        f = true;
                    }
                    if (itemSellsMatcher19.find()){
                        agrarian.getSells().add(SellType.RYBA);
                        f = true;
                    }
                    if (itemSellsMatcher20.find()){
                        agrarian.getSells().add(SellType.BOROSHNO);
                        f = true;
                    }
                    if (itemSellsMatcher21.find()){
                        agrarian.getSells().add(SellType.TSUKOR);
                        f = true;
                    }
                    if (itemSellsMatcher22.find()){
                        agrarian.getSells().add(SellType.MOLOKO);
                        f = true;
                    }
                    if (itemSellsMatcher23.find()){
                        agrarian.getSells().add(SellType.HLIB);
                        f = true;
                    }
                    if (itemSellsMatcher24.find()){
                        agrarian.getSells().add(SellType.MASLO);
                        f = true;
                    }
                    if (itemSellsMatcher25.find()){
                        agrarian.getSells().add(SellType.KONDYTERSKA);
                        f = true;
                    }
                    if (itemSellsMatcher26.find()){
                        agrarian.getSells().add(SellType.SPYRTOVA);
                        f = true;
                    }
                    if (itemSellsMatcher27.find()){
                        agrarian.getSells().add(SellType.MAKARONNA);
                        f = true;
                    }
                    if (itemSellsMatcher28.find()){
                        agrarian.getSells().add(SellType.PYVOVARNA);
                        f = true;
                    }
                    if (itemSellsMatcher29.find()){
                        agrarian.getSells().add(SellType.VYNOROBNA);
                        f = true;
                    }
                    if (itemSellsMatcher30.find()){
                        agrarian.getSells().add(SellType.KRUPY);
                        f = true;
                    }
                    if (itemSellsMatcher31.find()){
                        agrarian.getSells().add(SellType.KONSERVY);
                        f = true;
                    }
                    if (itemSellsMatcher32.find()){
                        agrarian.getSells().add(SellType.TUTUN);
                        f = true;
                    }
                    if (itemSellsMatcher33.find()){
                        agrarian.getSells().add(SellType.LIS_TA_VYROBY_Z_DEREVA);
                        f = true;
                    }
                    if (itemSellsMatcher34.find()){
                        agrarian.getSells().add(SellType.ROSLYNNYTSTVO);
                        agrarian.getSells().add(SellType.TVARYNNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher35.find()){
                        agrarian.getSells().add(SellType.BUDIVELNA_GALUZ_I_REMONT);
                        f = true;
                    }
                    if (itemSellsMatcher36.find()){
                        agrarian.getSells().add(SellType.NAFTA_I_PALYVO);
                        f = true;
                    }
                    if (itemSellsMatcher37.find()){
                        agrarian.getSells().add(SellType.ZASOBY_ZAHYSTU);
                        f = true;
                    }
                    if (itemSellsMatcher38.find()){
                        agrarian.getSells().add(SellType.INSHA_SFERA);
                        f = true;
                    }
                    if (itemSellsMatcher39.find()){
                        agrarian.getSells().add(SellType.TRANSPORT);
                        f = true;
                    }
                }
                if (!f && !sells.trim().isEmpty()){
                    Pattern itemSellsPattern40 = Pattern.compile(".*торг.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher40 = itemSellsPattern40.matcher(sells2);
                    if (itemSellsMatcher40.find()){
                        agrarian.getSells().add(SellType.INSHA_TORGIVLYA);
                    } else {
                        agrarian.getSells().add(SellType.INSHA_SFERA);
                    }
                }

                String input = paragraph.select("p").get(4).getElementsByIndexEquals(1).text();
                Pattern pattern = Pattern.compile("\\d{5,10}");

                Matcher matcher = pattern.matcher(input);

                if (matcher.find()) {
                    agrarian.setEdrpou(paragraph.select("p").get(4).getElementsByIndexEquals(1).text());
                } else {
                    agrarian.setServices(paragraph.select("p").get(4).getElementsByIndexEquals(1).text());
                }

                agrarian.setArea(paragraph.select(".miniBlockCut p").get(0).lastElementChild().text());
                String phone = paragraph.select(".miniBlockCut p").get(1).getElementsByIndexEquals(1).text();
                Pattern p = Pattern.compile("(?<=^|,|\\.|;|/)([^,;/.\\(]*(\\([^\\)]*\\))?[^,.;/\\(]*)*");
                Matcher m = p.matcher(phone);
                while (m.find()) {
                    String res= "";
                    if (m.group().isEmpty()) continue;
                    if (Character.isWhitespace(m.group().charAt(0))){
                        res = m.group().substring(1);
                    } else {
                        res = m.group();
                    }
                    agrarian.getPhones().add(res);
                }
                String email = paragraph.select(".miniBlockCut p").get(2).getElementsByIndexEquals(1).text();
                Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
                Matcher emailMatcher = emailPattern.matcher(email);
                while (emailMatcher.find()) {
                    String em = emailMatcher.group();
                    agrarian.getEmails().add(em);
                }
                agrarian.setWebsite(paragraph.select(".miniBlockCut p").get(3).getElementsByIndexEquals(1).text());
                agrarian.setVillageCouncil(paragraph.select(".miniBlockCut p").get(4).getElementsByIndexEquals(1).text());

                agrarianRepository.save(agrarian);
            } else {
                VillageCouncil villageCouncil = new VillageCouncil();
                List<String> phones = new ArrayList<>();
                List<String> emails = new ArrayList<>();
                List<String> villages = new ArrayList<>();
                Set<SellType> sellsArray = new HashSet<>();
                villageCouncil.setPhones(phones);
                villageCouncil.setEmails(emails);
                villageCouncil.setVillages(villages);
                villageCouncil.setSells(sellsArray);
                villageCouncil.setOblast(obl);
                villageCouncil.setOldRegion(reg);

                Element imageElement = paragraph.select(".miniPic img").first();
                String imageUrl = imageElement.attr("src");
                imageUrl = imageUrl.replaceAll(" ", "");
                if (!imageUrl.equals("")) {
                    if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                        imageUrl = "https://www.idcompass.com" + imageUrl;
                        Photo photo = downloadImage(imageUrl);
                        villageCouncil.setImage(photo);
                    }
                }
                villageCouncil.setTitle(paragraph.select("h3").text());
                villageCouncil.setAddress(paragraph.select("p").get(0).text());
                villageCouncil.setHead(paragraph.select("p").get(1).getElementsByIndexEquals(1).text());
                boolean f = false;
                String sells2 = paragraph.select("p").get(2).getElementsByIndexEquals(1).text();
                String sells1 = sells2.replaceAll("\\*", "");
                String sells = sells1.replaceAll("\\?", ",");
                Pattern pSells = Pattern.compile("(?<=^|,|\\.|;|/)([^,;/.\\(]*(\\([^\\)]*\\))?[^,.;/\\(]*)*");
                Matcher mSells = pSells.matcher(sells);
                while (mSells.find()) {
                    String resS= "";
                    if (!mSells.group().trim().isEmpty()) {
                        resS =mSells.group().trim().toLowerCase();
                    } else {
                        resS = mSells.group().toLowerCase();
                    }
                    if (resS.isEmpty()) continue;
                    Pattern itemSellsPattern1 = Pattern.compile("Рослинництво.*|рослинництв.*|растени.*|растериеводство|росл.*|хвоя|хвой.*|.*насінн.*|.*ландшафт.*|.*семен.*|семечки|сорго|.*ліан.*|.*лиан.*|киви|теплиц.*|тепличн.*|.*декоратив.*|.*декоритив.*|.*коренепл.*|.*корнепл.*|конопля|скловолокно|.*однорічн.*|.*дворічн.*|.*багаторічн.*|кореандр|коріандр|кориандр|посівн.* матеріал.*|насыння|.*лікарськ.*|.*трави.*|сосна.*|дуб|акація|ківі|.*отруби.*|післяурожайна.*|.*землеробство|.*вирщування с/г культур.*|швидкорастучі та якісні породи дерев", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher1 = itemSellsPattern1.matcher(resS);
                    Pattern itemSellsPattern2 = Pattern.compile(".*зерн.*|.*пшен.*|.*пшон.*|.*жит.*|жыт|ячм.*|греч.*|овес|просо|рожь|куку.*|кукру.*|бобов.*|бобві|боби|квасоля|соя.*|сої|сою|чечевица|сочевиц.*|горох|зенові|елеватор|рис|злак.*|висів.*|.*озим.*|ярих|маис|маїс|тритикале|фасоль|вівса", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher2 = itemSellsPattern2.matcher(resS);
                    Pattern itemSellsPattern3 = Pattern.compile("технічн.*|технически.*|комбікорми|корм.*|жмих|цукровий буряк|цукрові буряки|цукровий завод|.*олійн.*|.*масляничн.*|.*маслянічн.*|.*маслич.*|семечка|подсолн.*|.*сонячни.*|.*соняшни.*|рицина|рипак|рапс|ріпак|суріпа|рижій|гірчи.*|.*горчи.*|мак|кунжут|арахіс|перила|лялеманція|сафлор|ефіроолійн.*|коріандр|кмин|м'ята|шавлія|лаванда|фенхель|аніс|прядивні|льон|лен|коноплі|бавовник|силос|сіно|сено|солома|люцерн.*|еспарцет|.*рижій.*|.*рижію.*|суданка", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher3 = itemSellsPattern3.matcher(resS);
                    Pattern itemSellsPattern4 = Pattern.compile("кормові|макух.*|жмих.*|жмых.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher4 = itemSellsPattern4.matcher(resS);
                    Pattern itemSellsPattern5 = Pattern.compile("овоч.*|овощ.*|томат.*|.*помідо.*|помидор.*|карто.*|капус.*|свекл.*|морк.*|огур.*|огір.*|цибул.*|буряк.*|гриб.*|печериці|кабач.*|репа|баклажан.*|лук.*|зелень.*|укроп|петрушка|кинза|шпинат|щавель|руккола|салат|мята|базилик|.*бульбоплод.*|.*перець.*|селера|редиска|картапля|часник|чеснок|редис.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher5 = itemSellsPattern5.matcher(resS);
                    Pattern itemSellsPattern6 = Pattern.compile("кавун.*|диня|дині|дыня|.*арбуз.*|баштан.*|.*бахч.*|тыкв.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher6 = itemSellsPattern6.matcher(resS);
                    Pattern itemSellsPattern7 = Pattern.compile("сад.*|ябл.*|фрукт.|ягод.*|.*ягід.*|клубни.*|.*полуни.*|лохин.*|поричка.*|ожина|журавлина|чорниця|горобина|ежевика|алича|.*обліп.*|суниц.*|малин.*|смород.*|хмель|хміль|саженцы|кісточк.*|.*косточков.*|плодов.*|плоди|вишн.*|груш.*|земляни.*|сливы|сливу|сливи|.*слива|.*черешн.*|плодівниц.*|.*горіх.*|плоды.*|абрикос|персик|цитрусових|сажанці|кустарники|питомник|аґрус|жимолость|орехи", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher7 = itemSellsPattern7.matcher(resS);
                    Pattern itemSellsPattern8 = Pattern.compile("виноград.*|виниград", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher8 = itemSellsPattern8.matcher(resS);
                    Pattern itemSellsPattern9 = Pattern.compile("квіт.*|квытыв|рози|розы|цветы|троянди", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher9 = itemSellsPattern9.matcher(resS);
                    Pattern itemSellsPattern10 = Pattern.compile(".*твар.*|тванинництво|животнов.*|зоопарк.*|мисливство|шерсть|кози|кіз|кроли.*|.*сперм.*|.*крол.*|.*равлик.*|ферма|.*страус.*|звіроловство|.*вовн.*");
                    Matcher itemSellsMatcher10 = itemSellsPattern10.matcher(resS);
                    Pattern itemSellsPattern11 = Pattern.compile("скот.*|коров.*|коні|.*коне.*|телят.*|худоб.*|ВРХ|врх|крх|крс|КРС|буйволи|кінний туризм|пасовища|конярство|кіннозаводство|продукція конярства", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher11 = itemSellsPattern11.matcher(resS);
                    Pattern itemSellsPattern12 = Pattern.compile("свин.*|.*поросят.*|хряки|ландрас.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher12 = itemSellsPattern12.matcher(resS);
                    Pattern itemSellsPattern13 = Pattern.compile("вівц.*|вівчарств.*|овц.*|баран.*|овец.*|овеч.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher13 = itemSellsPattern13.matcher(resS);
                    Pattern itemSellsPattern14 = Pattern.compile(".*птиця|птиц.*|пттиця|птах.*|перепілк.*|.*перепеляч.*|куре.*|курк.*|курч.*|.*яйц.*|яєць|інкуба.*|инкубац.*|бролери|бройл.*|уток|гус.*|.*качки.*|.*качок.*|каченят|качині|кури|куря.*|.*пташинниц.*|циплята|звірі", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher14 = itemSellsPattern14.matcher(resS);
//                    Pattern itemSellsPattern15 = Pattern.compile("ліс.*|Ліс.*");
//                    Matcher itemSellsMatcher15 = itemSellsPattern15.matcher(resS);
                    Pattern itemSellsPattern16 = Pattern.compile("бдж.*|мед|пчолосім.*|.*пасіка.*|пчело.*|.*вуликів.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher16 = itemSellsPattern16.matcher(resS);
                    Pattern itemSellsPattern17 = Pattern.compile(".*харч.*|.*олія.*|.*олії.*|олію|.*круп.*|.*кулинарный.*|соки|.*вода.*|.*воду.*|.*води.*|квас|.*напо.*|чай|жири|оцет|.*пищевое.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher17 = itemSellsPattern17.matcher(resS);
                    Pattern itemSellsPattern18 = Pattern.compile("м'яс.*|мяс.*|м’яс.*|м\"яс.*|.*м‘яса.*|ковбас.*|ялович.*|ліверн.*|сосиски|сардельки|м'ясн.*|говядина|копченос.*|бекон", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher18 = itemSellsPattern18.matcher(resS);
                    Pattern itemSellsPattern19 = Pattern.compile(".*риб.*|.*рыб.*|.*ставкове господарство|білого амура|товстолоба|судака|сома|щуки|осетер.*|білуга|стерлядь|севрюга|.*аквакультура.*|морепродукти|.*ракопод.*|.*молюск.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher19 = itemSellsPattern19.matcher(resS);
                    Pattern itemSellsPattern20 = Pattern.compile(".*борошн.*|мука|муку", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher20 = itemSellsPattern20.matcher(resS);
                    Pattern itemSellsPattern21 = Pattern.compile("цукор|сахар|.*цукру.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher21 = itemSellsPattern21.matcher(resS);
                    Pattern itemSellsPattern22 = Pattern.compile("молок.*|молоч.*|.*сири|сир|бринза|вершки|кефір|кумис.*|кумыс", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher22 = itemSellsPattern22.matcher(resS);
                    Pattern itemSellsPattern23 = Pattern.compile("хліб.*|хлеб.*|пекарня", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher23 = itemSellsPattern23.matcher(resS);
                    Pattern itemSellsPattern24 = Pattern.compile("масло|масла", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher24 = itemSellsPattern24.matcher(resS);
                    Pattern itemSellsPattern25 = Pattern.compile("кондитер.*|торт.*|тістеч.*|печив.*|булоч.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher25 = itemSellsPattern25.matcher(resS);
                    Pattern itemSellsPattern26 = Pattern.compile("спирт.*|.*алкогольн.*|коняк|бренді", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher26 = itemSellsPattern26.matcher(resS);
                    Pattern itemSellsPattern27 = Pattern.compile("макаро.*|бакалія", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher27 = itemSellsPattern27.matcher(resS);
                    Pattern itemSellsPattern28 = Pattern.compile("пиво", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher28 = itemSellsPattern28.matcher(resS);
                    Pattern itemSellsPattern29 = Pattern.compile("вино|вина|винпродукція", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher29 = itemSellsPattern29.matcher(resS);
                    Pattern itemSellsPattern30 = Pattern.compile("крупи|крупы", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher30 = itemSellsPattern30.matcher(resS);
                    Pattern itemSellsPattern31 = Pattern.compile("консер.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher31 = itemSellsPattern31.matcher(resS);
                    Pattern itemSellsPattern32 = Pattern.compile("тютю.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher32 = itemSellsPattern32.matcher(resS);
                    Pattern itemSellsPattern33 = Pattern.compile("ліс.*|лес|дрова|пиломатеріали|дерево|.*древесина.*|.*столяр.*|.*деревин.*|.*дошка.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher33 = itemSellsPattern33.matcher(resS);
                    Pattern itemSellsPattern34 = Pattern.compile("змішане сільське господарство|.*сільськогоспод.*|.*выращивания сельскохозяйственных культур|.*виробництво, переробка та реалізація с/г продукції.*|продукція змішаного сільського господар-ства|змішане сільське господар-ство", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher34 = itemSellsPattern34.matcher(resS);
                    Pattern itemSellsPattern35 = Pattern.compile(".*будів.*|.*кірпіч.*|.*пісок.*|.*піск.*|.*граві.*|глину.*|глина.*|.*каолін.*|.*цегл.*|.*строит.*|.*строительн.*|.*підлогу, матеріали покриття.*|.*Плити труби іншіі вироби із пластмаси.*|.*мебе.*|.*мебл.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher35 = itemSellsPattern35.matcher(resS);
                    Pattern itemSellsPattern36 = Pattern.compile(".*нафт.*|.*паливо.*|.*пальним.*|.*вугіл.*|.*торговля топливом.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher36 = itemSellsPattern36.matcher(resS);
                    Pattern itemSellsPattern37 = Pattern.compile(".*засоби.* захисту.*|.*хімі.*|.*добрива.*|.*хімпрод.*|.*удобрени.*|.*сзр.*|.*хім продукти.*|трихограму", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher37 = itemSellsPattern37.matcher(resS);
                    Pattern itemSellsPattern38 = Pattern.compile(".*інша сфера.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher38 = itemSellsPattern38.matcher(resS);
                    Pattern itemSellsPattern39 = Pattern.compile(".*транспорт.*|.*перевез.*|подшипники|сальник|ремни|смазки|звездочки|.*машинами.*|.*мащини.*|.*автомоб.*|навантажувачі|.*технік.*|.*трактор.*|.*комбайн.*|.*запчаст.*|сельхоз технику|.*Аренда и лизинг сельскохозяйственной техники и оборудования.*|.*с/х техника.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher39 = itemSellsPattern39.matcher(resS);

                    if (itemSellsMatcher1.find()){
                        villageCouncil.getSells().add(SellType.ROSLYNNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher2.find()){
                        villageCouncil.getSells().add(SellType.ZERNOVI);
                        f = true;
                    }
                    if (itemSellsMatcher3.find()){
                        villageCouncil.getSells().add(SellType.TEKHNICHNI);
                        f = true;
                    }
                    if (itemSellsMatcher4.find()){
                        villageCouncil.getSells().add(SellType.KORMOVI);
                        f = true;
                    }
                    if (itemSellsMatcher5.find()){
                        villageCouncil.getSells().add(SellType.OVOCHEVI);
                        f = true;
                    }
                    if (itemSellsMatcher6.find()){
                        villageCouncil.getSells().add(SellType.BASHTANNI);
                        f = true;
                    }

                    if (itemSellsMatcher7.find()){
                        villageCouncil.getSells().add(SellType.SADIVNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher8.find()){
                        villageCouncil.getSells().add(SellType.VYNOGRADARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher9.find()){
                        villageCouncil.getSells().add(SellType.KVITKARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher10.find()){
                        villageCouncil.getSells().add(SellType.TVARYNNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher11.find()){
                        villageCouncil.getSells().add(SellType.SKOTARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher12.find()){
                        villageCouncil.getSells().add(SellType.SVYNARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher13.find()){
                        villageCouncil.getSells().add(SellType.VIVCHARSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher14.find()){
                        villageCouncil.getSells().add(SellType.PTAKHIVNYTSTVO);
                        f = true;
                    }
//                    if (itemSellsMatcher15.find()){
//                        agrarian.getSells().add(SellType.RYBNYTSTVO);
//                        f = true;
//                    }
                    if (itemSellsMatcher16.find()){
                        villageCouncil.getSells().add(SellType.BJILNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher17.find()){
                        villageCouncil.getSells().add(SellType.HARCHOVA_PROMYSLOVIST);
                        f = true;
                    }
                    if (itemSellsMatcher18.find()){
                        villageCouncil.getSells().add(SellType.MYASO);
                        f = true;
                    }
                    if (itemSellsMatcher19.find()){
                        villageCouncil.getSells().add(SellType.RYBA);
                        f = true;
                    }
                    if (itemSellsMatcher20.find()){
                        villageCouncil.getSells().add(SellType.BOROSHNO);
                        f = true;
                    }
                    if (itemSellsMatcher21.find()){
                        villageCouncil.getSells().add(SellType.TSUKOR);
                        f = true;
                    }
                    if (itemSellsMatcher22.find()){
                        villageCouncil.getSells().add(SellType.MOLOKO);
                        f = true;
                    }
                    if (itemSellsMatcher23.find()){
                        villageCouncil.getSells().add(SellType.HLIB);
                        f = true;
                    }
                    if (itemSellsMatcher24.find()){
                        villageCouncil.getSells().add(SellType.MASLO);
                        f = true;
                    }
                    if (itemSellsMatcher25.find()){
                        villageCouncil.getSells().add(SellType.KONDYTERSKA);
                        f = true;
                    }
                    if (itemSellsMatcher26.find()){
                        villageCouncil.getSells().add(SellType.SPYRTOVA);
                        f = true;
                    }
                    if (itemSellsMatcher27.find()){
                        villageCouncil.getSells().add(SellType.MAKARONNA);
                        f = true;
                    }
                    if (itemSellsMatcher28.find()){
                        villageCouncil.getSells().add(SellType.PYVOVARNA);
                        f = true;
                    }
                    if (itemSellsMatcher29.find()){
                        villageCouncil.getSells().add(SellType.VYNOROBNA);
                        f = true;
                    }
                    if (itemSellsMatcher30.find()){
                        villageCouncil.getSells().add(SellType.KRUPY);
                        f = true;
                    }
                    if (itemSellsMatcher31.find()){
                        villageCouncil.getSells().add(SellType.KONSERVY);
                        f = true;
                    }
                    if (itemSellsMatcher32.find()){
                        villageCouncil.getSells().add(SellType.TUTUN);
                        f = true;
                    }
                    if (itemSellsMatcher33.find()){
                        villageCouncil.getSells().add(SellType.LIS_TA_VYROBY_Z_DEREVA);
                        f = true;
                    }
                    if (itemSellsMatcher34.find()){
                        villageCouncil.getSells().add(SellType.ROSLYNNYTSTVO);
                        villageCouncil.getSells().add(SellType.TVARYNNYTSTVO);
                        f = true;
                    }
                    if (itemSellsMatcher35.find()){
                        villageCouncil.getSells().add(SellType.BUDIVELNA_GALUZ_I_REMONT);
                        f = true;
                    }
                    if (itemSellsMatcher36.find()){
                        villageCouncil.getSells().add(SellType.NAFTA_I_PALYVO);
                        f = true;
                    }
                    if (itemSellsMatcher37.find()){
                        villageCouncil.getSells().add(SellType.ZASOBY_ZAHYSTU);
                        f = true;
                    }
                    if (itemSellsMatcher38.find()){
                        villageCouncil.getSells().add(SellType.INSHA_SFERA);
                        f = true;
                    }
                    if (itemSellsMatcher39.find()){
                        villageCouncil.getSells().add(SellType.TRANSPORT);
                        f = true;
                    }
                }
                if (!f && !sells.trim().isEmpty()){
                    Pattern itemSellsPattern40 = Pattern.compile(".*торг.*", Pattern.CASE_INSENSITIVE);
                    Matcher itemSellsMatcher40 = itemSellsPattern40.matcher(sells2);
                    if (itemSellsMatcher40.find()){
                        villageCouncil.getSells().add(SellType.INSHA_TORGIVLYA);
                    } else {
                        villageCouncil.getSells().add(SellType.INSHA_SFERA);
                    }
                }
                villageCouncil.setServices(paragraph.select("p").get(4).getElementsByIndexEquals(1).text());
                villageCouncil.setArea(paragraph.select(".miniBlockCut p").get(0).lastElementChild().text());

                String phone = paragraph.select(".miniBlockCut p").get(1).getElementsByIndexEquals(1).text();
                Pattern p = Pattern.compile("(?<=^|,|\\.|;|/)([^,;/.\\(]*(\\([^\\)]*\\))?[^,.;/\\(]*)*");
                Matcher m = p.matcher(phone);
                while (m.find()) {
                    String res= "";
                    if (res.isEmpty()) continue;
                    if (Character.isWhitespace(m.group().charAt(0))){
                        res = m.group().substring(1);
                    } else {
                        res = m.group();
                    }
                    villageCouncil.getPhones().add(res);
                }
                String email = paragraph.select(".miniBlockCut p").get(2).getElementsByIndexEquals(1).text();
                Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
                Matcher emailMatcher = emailPattern.matcher(email);
                while (emailMatcher.find()) {
                    String em = emailMatcher.group();
                    villageCouncil.getEmails().add(em);
                }
                villageCouncil.setWebsite(paragraph.select(".miniBlockCut p").get(3).getElementsByIndexEquals(1).text());
                String vill = paragraph.select(".miniBlockCut p").get(4).getElementsByIndexEquals(1).text();
                String[] words = vill.split(",\\s*");
                for(String word : words) {
                    villageCouncil.getVillages().add(word);
                }

                villageCouncilRepository.save(villageCouncil);
            }
        }
    }
//    public void parseRegion(String region, String obl) throws IOException {
//        Document doc = Jsoup.parse(region);
//        Elements paragraphs = doc.select("#pageContent #blocksContent .miniblock");
//        for (Element paragraph : paragraphs) {
//                Agrarian agrarian = new Agrarian();
//                List<String> phones = new ArrayList<>();
//                List<String> emails = new ArrayList<>();
//                Set<SellType> sellsArray = new HashSet<>();
//                agrarian.setPhones(phones);
//                agrarian.setEmails(emails);
//                agrarian.setSells(sellsArray);
//                agrarian.setOblast(obl);
//                agrarian.setForOblastOnly(true);
//
//                Element imageElement = paragraph.select(".miniPic img").first();
//                String imageUrl = imageElement.attr("src");
////				imageUrl = imageUrl.replaceAll(" ", "");
//                if (!imageUrl.equals("")) {
//                    if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
//                        imageUrl = "https://www.idcompass.com" + imageUrl;
//                        Photo photo = downloadImage(imageUrl);
//                        agrarian.setImage(photo);
//                    }
//                }
//                agrarian.setTitle(paragraph.select("h3").text());
//                agrarian.setAddress(paragraph.select("p").get(0).text());
//                agrarian.setHead(paragraph.select("p").get(1).getElementsByIndexEquals(1).text());
//                boolean f = false;
//                String sells2 = paragraph.select("p").get(2).getElementsByIndexEquals(1).text();
//                String sells1 = sells2.replaceAll("\\*", "");
//                String sells = sells1.replaceAll("\\?", ",");
//                Pattern pSells = Pattern.compile("(?<=^|,|\\.|;|/)([^,;/.\\(]*(\\([^\\)]*\\))?[^,.;/\\(]*)*");
//                Matcher mSells = pSells.matcher(sells);
//                while (mSells.find()) {
//                    String resS= "";
//                    if (!mSells.group().trim().isEmpty()) {
//                        resS =mSells.group().trim().toLowerCase();
//                    } else {
//                        resS = mSells.group().toLowerCase();
//                    }
//                    if (resS.isEmpty()) continue;
////                    checkEnumType(resS);
//                    Pattern itemSellsPattern1 = Pattern.compile("Рослинництво.*|рослинництв.*|растени.*|растериеводство|росл.*|хвоя|хвой.*|.*насінн.*|.*ландшафт.*|.*семен.*|семечки|сорго|.*ліан.*|.*лиан.*|киви|теплиц.*|тепличн.*|.*декоратив.*|.*декоритив.*|.*коренепл.*|.*корнепл.*|конопля|скловолокно|.*однорічн.*|.*дворічн.*|.*багаторічн.*|кореандр|коріандр|кориандр|посівн.* матеріал.*|насыння|.*лікарськ.*|.*трави.*|сосна.*|дуб|акація|ківі|.*отруби.*|післяурожайна.*|.*землеробство|.*вирщування с/г культур.*|швидкорастучі та якісні породи дерев", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher1 = itemSellsPattern1.matcher(resS);
//                    Pattern itemSellsPattern2 = Pattern.compile(".*зерн.*|.*пшен.*|.*пшон.*|.*жит.*|жыт|ячм.*|греч.*|овес|просо|рожь|куку.*|кукру.*|бобов.*|бобві|боби|квасоля|соя.*|сої|сою|чечевица|сочевиц.*|горох|зенові|елеватор|рис|злак.*|висів.*|.*озим.*|ярих|маис|маїс|тритикале|фасоль|вівса", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher2 = itemSellsPattern2.matcher(resS);
//                    Pattern itemSellsPattern3 = Pattern.compile("технічн.*|технически.*|комбікорми|корм.*|жмих|цукровий буряк|цукрові буряки|цукровий завод|.*олійн.*|.*масляничн.*|.*маслянічн.*|.*маслич.*|семечка|подсолн.*|.*сонячни.*|.*соняшни.*|рицина|рипак|рапс|ріпак|суріпа|рижій|гірчи.*|.*горчи.*|мак|кунжут|арахіс|перила|лялеманція|сафлор|ефіроолійн.*|коріандр|кмин|м'ята|шавлія|лаванда|фенхель|аніс|прядивні|льон|лен|коноплі|бавовник|силос|сіно|сено|солома|люцерн.*|еспарцет|.*рижій.*|.*рижію.*|суданка", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher3 = itemSellsPattern3.matcher(resS);
//                    Pattern itemSellsPattern4 = Pattern.compile("кормові|макух.*|жмих.*|жмых.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher4 = itemSellsPattern4.matcher(resS);
//                    Pattern itemSellsPattern5 = Pattern.compile("овоч.*|овощ.*|томат.*|.*помідо.*|помидор.*|карто.*|капус.*|свекл.*|морк.*|огур.*|огір.*|цибул.*|буряк.*|гриб.*|печериці|кабач.*|репа|баклажан.*|лук.*|зелень.*|укроп|петрушка|кинза|шпинат|щавель|руккола|салат|мята|базилик|.*бульбоплод.*|.*перець.*|селера|редиска|картапля|часник|чеснок|редис.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher5 = itemSellsPattern5.matcher(resS);
//                    Pattern itemSellsPattern6 = Pattern.compile("кавун.*|диня|дині|дыня|.*арбуз.*|баштан.*|.*бахч.*|тыкв.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher6 = itemSellsPattern6.matcher(resS);
//                    Pattern itemSellsPattern7 = Pattern.compile("сад.*|ябл.*|фрукт.*|ягод.*|.*ягід.*|клубни.*|.*полуни.*|лохин.*|поричка.*|ожина|журавлина|чорниця|горобина|ежевика|алича|.*обліп.*|суниц.*|малин.*|смород.*|хмель|хміль|саженцы|кісточк.*|.*косточков.*|плодов.*|плоди|вишн.*|груш.*|земляни.*|сливы|сливу|сливи|.*слива|.*черешн.*|плодівниц.*|.*горіх.*|плоды.*|абрикос|персик|цитрусових|сажанці|кустарники|питомник|аґрус|жимолость|орехи", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher7 = itemSellsPattern7.matcher(resS);
//                    Pattern itemSellsPattern8 = Pattern.compile("виноград.*|виниград", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher8 = itemSellsPattern8.matcher(resS);
//                    Pattern itemSellsPattern9 = Pattern.compile("квіт.*|квытыв|рози|розы|цветы|троянди", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher9 = itemSellsPattern9.matcher(resS);
//                    Pattern itemSellsPattern10 = Pattern.compile(".*твар.*|тванинництво|животнов.*|зоопарк.*|мисливство|шерсть|кози|кіз|кроли.*|.*сперм.*|.*крол.*|.*равлик.*|ферма|.*страус.*|звіроловство|.*вовн.*");
//                    Matcher itemSellsMatcher10 = itemSellsPattern10.matcher(resS);
//                    Pattern itemSellsPattern11 = Pattern.compile("скот.*|коров.*|коні|.*коне.*|телят.*|худоб.*|ВРХ|врх|крх|крс|КРС|буйволи|кінний туризм|пасовища|конярство|кіннозаводство|продукція конярства", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher11 = itemSellsPattern11.matcher(resS);
//                    Pattern itemSellsPattern12 = Pattern.compile("свин.*|.*поросят.*|хряки|ландрас.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher12 = itemSellsPattern12.matcher(resS);
//                    Pattern itemSellsPattern13 = Pattern.compile("вівц.*|вівчарств.*|овц.*|баран.*|овец.*|овеч.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher13 = itemSellsPattern13.matcher(resS);
//                    Pattern itemSellsPattern14 = Pattern.compile(".*птиця|птиц.*|пттиця|птах.*|перепілк.*|.*перепеляч.*|куре.*|курк.*|курч.*|.*яйц.*|яєць|інкуба.*|инкубац.*|бролери|бройл.*|уток|гус.*|.*качки.*|.*качок.*|каченят|качині|кури|куря.*|.*пташинниц.*|циплята|звірі", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher14 = itemSellsPattern14.matcher(resS);
////                    Pattern itemSellsPattern15 = Pattern.compile("ліс.*|Ліс.*");
////                    Matcher itemSellsMatcher15 = itemSellsPattern15.matcher(resS);
//                    Pattern itemSellsPattern16 = Pattern.compile("бдж.*|мед|пчолосім.*|.*пасіка.*|пчело.*|.*вуликів.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher16 = itemSellsPattern16.matcher(resS);
//                    Pattern itemSellsPattern17 = Pattern.compile(".*харч.*|.*олія.*|.*олії.*|олію|.*круп.*|.*кулинарный.*|соки|.*вода.*|.*воду.*|.*води.*|квас|.*напо.*|чай|жири|оцет|.*пищевое.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher17 = itemSellsPattern17.matcher(resS);
//                    Pattern itemSellsPattern18 = Pattern.compile("м'яс.*|мяс.*|м’яс.*|м\"яс.*|.*м‘яса.*|ковбас.*|ялович.*|ліверн.*|сосиски|сардельки|м'ясн.*|говядина|копченос.*|бекон", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher18 = itemSellsPattern18.matcher(resS);
//                    Pattern itemSellsPattern19 = Pattern.compile(".*риб.*|.*рыб.*|.*ставкове господарство|білого амура|товстолоба|судака|сома|щуки|осетер.*|білуга|стерлядь|севрюга|.*аквакультура.*|морепродукти|.*ракопод.*|.*молюск.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher19 = itemSellsPattern19.matcher(resS);
//                    Pattern itemSellsPattern20 = Pattern.compile(".*борошн.*|мука|муку", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher20 = itemSellsPattern20.matcher(resS);
//                    Pattern itemSellsPattern21 = Pattern.compile("цукор|сахар|.*цукру.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher21 = itemSellsPattern21.matcher(resS);
//                    Pattern itemSellsPattern22 = Pattern.compile("молок.*|молоч.*|.*сири|сир|бринза|вершки|кефір|кумис.*|кумыс", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher22 = itemSellsPattern22.matcher(resS);
//                    Pattern itemSellsPattern23 = Pattern.compile("хліб.*|хлеб.*|пекарня", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher23 = itemSellsPattern23.matcher(resS);
//                    Pattern itemSellsPattern24 = Pattern.compile("масло|масла", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher24 = itemSellsPattern24.matcher(resS);
//                    Pattern itemSellsPattern25 = Pattern.compile("кондитер.*|торт.*|тістеч.*|печив.*|булоч.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher25 = itemSellsPattern25.matcher(resS);
//                    Pattern itemSellsPattern26 = Pattern.compile("спирт.*|.*алкогольн.*|коняк|бренді", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher26 = itemSellsPattern26.matcher(resS);
//                    Pattern itemSellsPattern27 = Pattern.compile("макаро.*|бакалія", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher27 = itemSellsPattern27.matcher(resS);
//                    Pattern itemSellsPattern28 = Pattern.compile("пиво", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher28 = itemSellsPattern28.matcher(resS);
//                    Pattern itemSellsPattern29 = Pattern.compile("вино|вина|винпродукція", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher29 = itemSellsPattern29.matcher(resS);
//                    Pattern itemSellsPattern30 = Pattern.compile("крупи|крупы", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher30 = itemSellsPattern30.matcher(resS);
//                    Pattern itemSellsPattern31 = Pattern.compile("консер.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher31 = itemSellsPattern31.matcher(resS);
//                    Pattern itemSellsPattern32 = Pattern.compile("тютю.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher32 = itemSellsPattern32.matcher(resS);
//                    Pattern itemSellsPattern33 = Pattern.compile("ліс.*|лес|дрова|пиломатеріали|дерево|.*древесина.*|.*столяр.*|.*деревин.*|.*дошка.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher33 = itemSellsPattern33.matcher(resS);
//                    Pattern itemSellsPattern34 = Pattern.compile("змішане сільське господарство|.*сільськогоспод.*|.*выращивания сельскохозяйственных культур|.*виробництво, переробка та реалізація с/г продукції.*|продукція змішаного сільського господар-ства|змішане сільське господар-ство", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher34 = itemSellsPattern34.matcher(resS);
//                    Pattern itemSellsPattern35 = Pattern.compile(".*будів.*|.*кірпіч.*|.*пісок.*|.*піск.*|.*граві.*|глину.*|глина.*|.*каолін.*|.*цегл.*|.*строит.*|.*строительн.*|.*підлогу, матеріали покриття.*|.*Плити труби іншіі вироби із пластмаси.*|.*мебе.*|.*мебл.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher35 = itemSellsPattern35.matcher(resS);
//                    Pattern itemSellsPattern36 = Pattern.compile(".*нафт.*|.*паливо.*|.*пальним.*|.*вугіл.*|.*торговля топливом.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher36 = itemSellsPattern36.matcher(resS);
//                    Pattern itemSellsPattern37 = Pattern.compile(".*засоби.* захисту.*|.*хімі.*|.*добрива.*|.*хімпрод.*|.*удобрени.*|.*сзр.*|.*хім продукти.*|трихограму", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher37 = itemSellsPattern37.matcher(resS);
//                    Pattern itemSellsPattern38 = Pattern.compile(".*інша сфера.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher38 = itemSellsPattern38.matcher(resS);
//                    Pattern itemSellsPattern39 = Pattern.compile(".*транспорт.*|.*перевез.*|подшипники|сальник|ремни|смазки|звездочки|.*машинами.*|.*мащини.*|.*автомоб.*|навантажувачі|.*технік.*|.*трактор.*|.*комбайн.*|.*запчаст.*|сельхоз технику|.*Аренда и лизинг сельскохозяйственной техники и оборудования.*|.*с/х техника.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher39 = itemSellsPattern39.matcher(resS);
//
//                    if (itemSellsMatcher1.find()){
//                        agrarian.getSells().add(SellType.ROSLYNNYTSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher2.find()){
//                        agrarian.getSells().add(SellType.ZERNOVI);
//                        f = true;
//                    }
//                    if (itemSellsMatcher3.find()){
//                        agrarian.getSells().add(SellType.TEKHNICHNI);
//                        f = true;
//                    }
//                    if (itemSellsMatcher4.find()){
//                        agrarian.getSells().add(SellType.KORMOVI);
//                        f = true;
//                    }
//                    if (itemSellsMatcher5.find()){
//                        agrarian.getSells().add(SellType.OVOCHEVI);
//                        f = true;
//                    }
//                    if (itemSellsMatcher6.find()){
//                        agrarian.getSells().add(SellType.BASHTANNI);
//                        f = true;
//                    }
//
//                    if (itemSellsMatcher7.find()){
//                        agrarian.getSells().add(SellType.SADIVNYTSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher8.find()){
//                        agrarian.getSells().add(SellType.VYNOGRADARSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher9.find()){
//                        agrarian.getSells().add(SellType.KVITKARSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher10.find()){
//                        agrarian.getSells().add(SellType.TVARYNNYTSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher11.find()){
//                        agrarian.getSells().add(SellType.SKOTARSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher12.find()){
//                        agrarian.getSells().add(SellType.SVYNARSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher13.find()){
//                        agrarian.getSells().add(SellType.VIVCHARSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher14.find()){
//                        agrarian.getSells().add(SellType.PTAKHIVNYTSTVO);
//                        f = true;
//                    }
////                    if (itemSellsMatcher15.find()){
////                        agrarian.getSells().add(SellType.RYBNYTSTVO);
////                        f = true;
////                    }
//                    if (itemSellsMatcher16.find()){
//                        agrarian.getSells().add(SellType.BJILNYTSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher17.find()){
//                        agrarian.getSells().add(SellType.HARCHOVA_PROMYSLOVIST);
//                        f = true;
//                    }
//                    if (itemSellsMatcher18.find()){
//                        agrarian.getSells().add(SellType.MYASO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher19.find()){
//                        agrarian.getSells().add(SellType.RYBA);
//                        f = true;
//                    }
//                    if (itemSellsMatcher20.find()){
//                        agrarian.getSells().add(SellType.BOROSHNO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher21.find()){
//                        agrarian.getSells().add(SellType.TSUKOR);
//                        f = true;
//                    }
//                    if (itemSellsMatcher22.find()){
//                        agrarian.getSells().add(SellType.MOLOKO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher23.find()){
//                        agrarian.getSells().add(SellType.HLIB);
//                        f = true;
//                    }
//                    if (itemSellsMatcher24.find()){
//                        agrarian.getSells().add(SellType.MASLO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher25.find()){
//                        agrarian.getSells().add(SellType.KONDYTERSKA);
//                        f = true;
//                    }
//                    if (itemSellsMatcher26.find()){
//                        agrarian.getSells().add(SellType.SPYRTOVA);
//                        f = true;
//                    }
//                    if (itemSellsMatcher27.find()){
//                        agrarian.getSells().add(SellType.MAKARONNA);
//                        f = true;
//                    }
//                    if (itemSellsMatcher28.find()){
//                        agrarian.getSells().add(SellType.PYVOVARNA);
//                        f = true;
//                    }
//                    if (itemSellsMatcher29.find()){
//                        agrarian.getSells().add(SellType.VYNOROBNA);
//                        f = true;
//                    }
//                    if (itemSellsMatcher30.find()){
//                        agrarian.getSells().add(SellType.KRUPY);
//                        f = true;
//                    }
//                    if (itemSellsMatcher31.find()){
//                        agrarian.getSells().add(SellType.KONSERVY);
//                        f = true;
//                    }
//                    if (itemSellsMatcher32.find()){
//                        agrarian.getSells().add(SellType.TUTUN);
//                        f = true;
//                    }
//                    if (itemSellsMatcher33.find()){
//                        agrarian.getSells().add(SellType.LIS_TA_VYROBY_Z_DEREVA);
//                        f = true;
//                    }
//                    if (itemSellsMatcher34.find()){
//                        agrarian.getSells().add(SellType.ROSLYNNYTSTVO);
//                        agrarian.getSells().add(SellType.TVARYNNYTSTVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher35.find()){
//                        agrarian.getSells().add(SellType.BUDIVELNA_GALUZ_I_REMONT);
//                        f = true;
//                    }
//                    if (itemSellsMatcher36.find()){
//                        agrarian.getSells().add(SellType.NAFTA_I_PALYVO);
//                        f = true;
//                    }
//                    if (itemSellsMatcher37.find()){
//                        agrarian.getSells().add(SellType.ZASOBY_ZAHYSTU);
//                        f = true;
//                    }
//                    if (itemSellsMatcher38.find()){
//                        agrarian.getSells().add(SellType.INSHA_SFERA);
//                        f = true;
//                    }
//                    if (itemSellsMatcher39.find()){
//                        agrarian.getSells().add(SellType.TRANSPORT);
//                        f = true;
//                    }
//                }
//                if (!f && !sells.trim().isEmpty()){
//                    Pattern itemSellsPattern40 = Pattern.compile(".*торг.*", Pattern.CASE_INSENSITIVE);
//                    Matcher itemSellsMatcher40 = itemSellsPattern40.matcher(sells2);
//                    if (itemSellsMatcher40.find()){
//                        agrarian.getSells().add(SellType.INSHA_TORGIVLYA);
//                    } else {
//                        agrarian.getSells().add(SellType.INSHA_SFERA);
//                    }
//                }
//                agrarian.setServices(paragraph.select("p").get(4).getElementsByIndexEquals(1).text());
//                agrarian.setArea(paragraph.select(".miniBlockCut p").get(0).lastElementChild().text());
//                String phone = paragraph.select(".miniBlockCut p").get(1).getElementsByIndexEquals(1).text();
//                Pattern p = Pattern.compile("(?<=^|,|\\.|;|/)([^,;/.\\(]*(\\([^\\)]*\\))?[^,.;/\\(]*)*");
//                Matcher m = p.matcher(phone);
//                while (m.find()) {
//                    String res= "";
//                    if (m.group().isEmpty()) continue;
//                    if (Character.isWhitespace(m.group().charAt(0))){
//                        res = m.group().substring(1);
//                    } else {
//                        res = m.group();
//                    }
//                    agrarian.getPhones().add(res);
//                }
//                String email = paragraph.select(".miniBlockCut p").get(2).getElementsByIndexEquals(1).text();
//                Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
//                Matcher emailMatcher = emailPattern.matcher(email);
//                while (emailMatcher.find()) {
//                    String em = emailMatcher.group();
//                    agrarian.getEmails().add(em);
//                }
//                agrarian.setWebsite(paragraph.select(".miniBlockCut p").get(3).getElementsByIndexEquals(1).text());
//                agrarian.setVillageCouncil(paragraph.select(".miniBlockCut p").get(4).getElementsByIndexEquals(1).text());
//
//                agrarianRepository.save(agrarian);
//        }
//    }

    public Photo downloadImage(String imageUrl) throws IOException {
        int charPos = imageUrl.lastIndexOf("/");
        String picName = imageUrl.substring(charPos + 1);
        Photo imagePhoto = null;
        try (InputStream in = new BufferedInputStream(new URL(imageUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("src/main/java/com/abi/agro_back/pictures/"+picName)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            String filePath = "src/main/java/com/abi/agro_back/pictures/"+picName;
            String key = System.currentTimeMillis() + "" + picName;
            URL urlForDatabase = storageService.uploadPhoto1(key, filePath);
            imagePhoto = new Photo(key, urlForDatabase);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imagePhoto;
    }

}
