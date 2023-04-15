package io.project.tgregisterbot.service;

import com.vdurmont.emoji.EmojiParser;
import io.project.tgregisterbot.config.BotConfig;
import io.project.tgregisterbot.model.clients.User;
import io.project.tgregisterbot.model.clients.UserRepository;
import io.project.tgregisterbot.model.contacts.*;
import io.project.tgregisterbot.model.info.PriceList;
import io.project.tgregisterbot.model.info.PriceListRepository;
import io.project.tgregisterbot.model.info.Promo;
import io.project.tgregisterbot.model.info.PromoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PriceListRepository priceListRepository;
    @Autowired
    private PromoRepository promoRepository;
    @Autowired
    private AddressesRepository addressesRepository;
    @Autowired
    private PhoneNumbersRepository phoneNumbersRepository;
    @Autowired
    private SocialMediaRepository socialMediaRepository;

    final BotConfig config;
    static final String HELLO_TEXT = "Привіт!" +
            "Я допоможу тобі забронювати місце на масаж. Розкажи мені, коли тобі зручно записатися на масаж," +
            " і наш спеціаліст перевірить наявність вільних місць.";
    static final String HELP_TEXT = "Я можу допомогти вам забронювати місце на масаж " +
            "та відповісти на деякі запитання\n\n";
    static final String COMMAND = "Бажаєте записатись? Тисніть 'Записатись на масаж'.\n\n" +
            "Є питання? Тисніть 'Поставити питання'.\n\n" +
            "Введіть /price щоби переглянути прайс.\n\n" +
            "Введіть /promo щоб переглянути акції та спеціальні пропозиції.\n\n" +
            "Введіть /contacts щоб переглянути контактну інформацію.\n\n" +
            "Введіть /help щоб переглянути це повідомлення знову";


    static final String OWNER_HELP_TEXT = "Я допоможу з адмініструванням чат-боту.\n\n" +
            "Введіть /price щоб переглянути прайс та ID прайсу.\n\n" +
            "Введіть /address щоб переглянути адресу та ID адреси.\n\n" +
            "Введіть /promo щоб переглянути акції та спеціальні пропозиції та ID промоації.\n\n" +
            "Введіть /contacts щоб переглянути контактну інформацію та ID контактів.\n\n" +
            "Введіть /help щоб переглянути це повідомлення знову.";
    static final String PRICE_HELP = "Введіть '/add_price Назва пуслуги Ціна' щоб додати прайс.\n" +
            "(прик. /add_price Масаж 500 грн.)\n\n" +
            "Введіть '/delete_price ID прайса' щоб видалити прайс.\n" +
            "(прик. /delete_price 1)";
    static final String CONTACT_HELP = "Введіть '/add_contacts Адреса / Номер телефону' щоб додати адресу та прив'язати " +
            "до неї номер контактний номер телефону.\n" +
            "(прик. /add_contacts місто, назва вулиці, номер будинку ітд. / 0631112233)\n\n" +
            "Введіть '/delete_address ID адреси' щоб видалити адресу.\n" +
            "(прик. /delete_address 1)";
    static final String PHONE_NUM_HELP = "Введіть '/add_phone ID адреси / Номер телефону' щоб прив'язати ще один номер " +
            "телефону до адреси.\n" +
            "(прик. /add_phone 1 / 0632221133)\n\n" +
            "Введіть '/delete_phone ID номеру' щоб видалити номер телефону.\n" +
            "(прик. /delete_phone 1)";
    static final String SOCIAL_MEDIA_HELP = "Введіть '/add_social посилання на соціальну мережу'" +
            " щоб додати соціальну мережу.\n" +
            "(прик. /add_social yousocialmedia.соm/іd1234)\n\n" +
            "Введіть '/delete_social ID посилання' щоб видалити соціальну мережу.\n" +
            "(прик. /delete_social 1)";
    static final String PROMO_HELP = "Введіть '/add_promo промоакція'" +
            " щоб додати промоакцію.\n" +
            "(прик. /add_promo Знажка 10%)\n\n" +
            "Введіть '/delete_promo ID промоакції' щоб видалити промоакцію.\n" +
            "(прик. /delete_promo 1)";
    static final String SEND_HELP = "Введіть '/send повідомлення'" +
            " щоб відправити повідомлення всім користувачам чат-бота.\n" +
            "(прик. /send Привіт всім!)\n\n" +
            "Введіть 'send_to_id id кристувача / повідомлення'" +
            "щоб відправити повідомлення користувачу по його id.\n" +
            "(прик. /send_to_id 0000 / Привіт!)";

    static final String KEYBOARD_1 = "Записатись на масаж";
    static final String KEYBOARD_2 = "Поставити запитання";
    static final String INSTRUCTION_KEYBOARD_1 = "Напишіть, як до вас можна звертатись, який вид масажу вас цікавить," +
            " на який день та час вам зручно записатися." +
            " Обов'язково, напишіть будь ласка ваш номер телефону для зворотнього зв'язку.";
    static final String INSTRUCTION_KEYBOARD_2 = "Напишіть, як до вас можна звертатись та ваше питання." +
            " Обов'язково, напишіть будь ласка ваш номер телефону для зворотнього зв'язку";


    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listCommands = new ArrayList<>();
        listCommands.add(new BotCommand("/price", "Переглянути прайс"));
        listCommands.add(new BotCommand("/promo", "Акції та спеціальні пропозиції"));
        listCommands.add(new BotCommand("/contacts", "Контактна інформація"));
        listCommands.add(new BotCommand("/help", "Навігація"));
        try {
            this.execute(new SetMyCommands(listCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (config.getOwnerId() == chatId) {

                if (messageText.contains("/send")) {
                    var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                    var users = userRepository.findAll();
                    for (User user : users) {
                        prepareAndSendMessage(user.getChatId(), textToSend);
                    }
                }

                if (messageText.contains("/send_to_id")) {
                    var text = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                    if (text.contains("/")) {
                        String[] parts = text.split("/");
                        String textId = parts[0];
                        String textToSend = parts[1];

                        String idStr = textId.replaceAll("[^0-9.]", "").trim();
                        try {
                            long id = Long.parseLong(idStr);
                            prepareAndSendMessage(id, textToSend);
                        } catch (NumberFormatException e) {
                            prepareAndSendMessage(config.getOwnerId(),
                                    "Помилка, невірний формат повідомлення " + e.getMessage());
                        }
                    } else {
                        prepareAndSendMessage(config.getOwnerId(),
                                "Помилка, невірний формат повідомлення, перевірте коректність команди ");
                    }
                }

                if (messageText.contains("/add_promo")) {
                    var addPromo = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                    Promo promo = new Promo();
                    promo.setPromo(addPromo);
                    promoRepository.save(promo);
                }

                if (messageText.contains("/delete_promo")) {
                    String idStr = messageText.replaceAll("[^0-9.]", "").trim();
                    try {
                        long id = Long.parseLong(idStr);
                        promoRepository.deleteById(id);
                    } catch (NumberFormatException e) {
                        prepareAndSendMessage(config.getOwnerId(),
                                "Помилка, невірний формат повідомлення " + e.getMessage());
                    }
                }
                if (messageText.contains("/add_price")) {
                    var addPrice = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                    PriceList priceList = new PriceList();
                    priceList.setPrice(addPrice);
                    priceListRepository.save(priceList);
                }

                if (messageText.contains("/delete_price")) {
                    String idStr = messageText.replaceAll("[^0-9.]", "").trim();
                    try {
                        long id = Long.parseLong(idStr);
                        priceListRepository.deleteById(id);
                    } catch (NumberFormatException e) {
                        prepareAndSendMessage(config.getOwnerId(),
                                "Помилка, невірний формат повідомлення " + e.getMessage());
                    }

                }

                if (messageText.contains("/add_contacts")) {
                    var contacts = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));

                    if (contacts.contains("/")) {
                        String[] parts = contacts.split("/");
                        String addAddress = parts[0];
                        String addPhone = parts[1];

                        Addresses address = new Addresses();
                        PhoneNumbers phoneNumbers = new PhoneNumbers();

                        phoneNumbers.setPhone(addPhone);
                        address.setAddress(addAddress);
                        address.addPhones(phoneNumbers);
                        addressesRepository.save(address);

                        prepareAndSendMessage(config.getOwnerId(), addAddress + " " + addPhone);
                    } else {
                        prepareAndSendMessage(config.getOwnerId(),
                                "Помилка, невірний формат повідомлення, перевірте коректність команди ");
                    }
                }

                if (messageText.contains("/delete_address")) {
                    String idStr = messageText.replaceAll("[^0-9.]", "").trim();
                    try {
                        long id = Long.parseLong(idStr);
                        addressesRepository.deleteById(id);
                    } catch (NumberFormatException e) {
                        prepareAndSendMessage(config.getOwnerId(),
                                "Помилка, невірний формат повідомлення " + e.getMessage());
                    }
                }

                if (messageText.contains("/add_phone")) {
                    var message = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));

                    if (message.contains("/")) {
                        String[] parts = message.split("/");
                        String idStr = parts[0];
                        String phone = parts[1];

                        PhoneNumbers phoneNumber = new PhoneNumbers();
                        phoneNumber.setPhone(phone);

                        String addressIdStr = idStr.replaceAll("[^0-9.]", "").trim();
                        try {
                            long id = Long.parseLong(addressIdStr);


                            Optional<Addresses> addresses = addressesRepository.findById(id);
                            Addresses address = addresses.get();
                            phoneNumber.setAddress(address);
                            phoneNumbersRepository.save(phoneNumber);
                        } catch (NumberFormatException e) {
                            prepareAndSendMessage(config.getOwnerId(),
                                    "Помилка, невірний формат повідомлення " + e.getMessage());
                        }
                    } else {
                        prepareAndSendMessage(config.getOwnerId(),
                                "Помилка, невірний формат повідомлення, перевірте коректність команди ");
                    }
                }

                if (messageText.contains("/delete_phone")) {
                    String idStr = messageText.replaceAll("[^0-9.]", "").trim();
                    try {
                        long id = Long.parseLong(idStr);
                        phoneNumbersRepository.deleteById(id);
                    } catch (NumberFormatException e) {
                        prepareAndSendMessage(config.getOwnerId(),
                                "Помилка, невірний формат повідомлення " + e.getMessage());
                    }
                }

                if (messageText.contains("/add_social")) {
                    var addSocial = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                    SocialMedia socialMedia = new SocialMedia();
                    socialMedia.setLink(addSocial);
                    socialMediaRepository.save(socialMedia);
                }

                if (messageText.contains("/delete_social")) {
                    String idStr = messageText.replaceAll("[^0-9.]", "").trim();
                    try {
                        long id = Long.parseLong(idStr);
                        socialMediaRepository.deleteById(id);
                    } catch (NumberFormatException e) {
                        prepareAndSendMessage(config.getOwnerId(),
                                "Помилка, невірний формат повідомлення " + e.getMessage());
                    }
                }

                switch (messageText) {
                    case "/start":
                        prepareAndSendMessage(config.getOwnerId(),
                                OWNER_HELP_TEXT + "\n\n\n" +
                                        "Щоб додати інформацію або відправити повідомлення всім користувачам: " + "\n\n" +
                                        SEND_HELP + "\n\n" + PRICE_HELP + "\n\n" + CONTACT_HELP + "\n\n" + PROMO_HELP + "\n\n" +
                                        PHONE_NUM_HELP + "\n\n" + SOCIAL_MEDIA_HELP);
                        break;
                    case "/help":
                        prepareAndSendMessage(config.getOwnerId(), OWNER_HELP_TEXT + "\n\n\n" +
                                "Щоб додати інформацію або відправити повідомлення всім користувачам: " + "\n\n" +
                                SEND_HELP + "\n\n" + PRICE_HELP + "\n\n" + CONTACT_HELP + "\n\n" + PROMO_HELP + "\n\n" +
                                PHONE_NUM_HELP + "\n\n" + SOCIAL_MEDIA_HELP);
                        break;
                    case "/promo":
                        viewPromo(config.getOwnerId());
                        break;
                    case "/price":
                        viewPrice(config.getOwnerId());
                        break;
                    case "/address":
                        viewAddress(config.getOwnerId());
                        break;
                    case "/contacts":
                        viewContacts(config.getOwnerId());
                        break;
                    case "/phone":
                        viewPhone(config.getOwnerId());
                        break;
                    case "/social":
                        viewSocialMedia(config.getOwnerId());
                        break;

                }

            } else {

                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommentReceived(chatId);
                        break;
                    case "/price":
                        viewPrice(chatId);
                        break;
                    case "/promo":
                        viewPromo(chatId);
                        break;
                    case "/contacts":
                        viewSocialMedia(chatId);
                        viewContacts(chatId);
                        break;
                    case "/help":
                        prepareAndSendMessage(chatId, HELP_TEXT + "\n" + COMMAND);
                        break;
                    case KEYBOARD_1:
                        prepareAndSendMessage(config.getOwnerId(), messageText);
                        prepareAndSendMessage(chatId, INSTRUCTION_KEYBOARD_1);
                        break;
                    case KEYBOARD_2:
                        prepareAndSendMessage(config.getOwnerId(), messageText);
                        prepareAndSendMessage(chatId, INSTRUCTION_KEYBOARD_2);
                        break;
                }

            }
            if (!update.getMessage().getChatId().equals(config.getOwnerId()) && messageText.contains("0")) {
                prepareAndSendMessage(config.getOwnerId(), "Повідомлення надійшло від користувача: " +
                        update.getMessage().getChat().getFirstName() + "\n\n" + "ID користувача: " + chatId + "\n\n" + messageText);

            }
        }
    }

    private void startCommentReceived(long chatId) {
        sendMessage(chatId, HELLO_TEXT + "\n\n" + COMMAND);
    }

    private void registerUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()){
        var chatId = message.getChatId();
        var chat = message.getChat();
        User user = new User();
        user.setChatId(chatId);
        user.setUserName(chat.getUserName());
        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setRegisterAt(new Timestamp(System.currentTimeMillis()));

        userRepository.save(user);
    }

}
    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();


        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(KEYBOARD_1);
        row.add(KEYBOARD_2);

        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(replyKeyboardMarkup);



        executeMessage(message);
    }
    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        executeMessage(message);
    }
    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
        }
    }
    private void viewPrice(long chatId){
        List<PriceList> priceLists = (List<PriceList>) priceListRepository.findAll();
        for (PriceList priceList : priceLists){
            if(chatId == config.getOwnerId()){
                prepareAndSendMessage(chatId,"ID: "+priceList.getId()+" Price: "+priceList.getPrice());
            }else {
                prepareAndSendMessage(chatId, priceList.getPrice());
            }
        }
    }
    private void viewPromo(long chatId){
       List<Promo> promos = (List<Promo>) promoRepository.findAll();
       for (Promo promo : promos){
           if(chatId == config.getOwnerId()){
               prepareAndSendMessage(chatId,"ID: "+promo.getId()+" Promo: "+promo.getPromo());
           }else {
               prepareAndSendMessage(chatId, promo.getPromo());
           }
       }
    }
    private void viewAddress(long chatId){
        List<Addresses> addresses = (List<Addresses>) addressesRepository.findAll();
        for(Addresses address : addresses) {
            prepareAndSendMessage(chatId,
                    "ID: " + address.getId() + "Наш кабінет знаходеться за адресою: " + address.getAddress());
        }
    }
    private void viewPhone(long chatId){
        List<PhoneNumbers> phoneNumbers = (List<PhoneNumbers>) phoneNumbersRepository.findAll();
        for (PhoneNumbers phoneNumber : phoneNumbers){
                prepareAndSendMessage(chatId,
                        "ID: "+phoneNumber.getId()+"Номер телефону: "+phoneNumber.getPhone());
        }
    }
    private void viewSocialMedia(long chatId){
        List<SocialMedia> socialMedias = (List<SocialMedia>) socialMediaRepository.findAll();
        for (SocialMedia socialMedia : socialMedias){
            if(chatId == config.getOwnerId()){
                prepareAndSendMessage(chatId,
                        "ID: "+socialMedia.getId()+" Соціальні мережі: "+socialMedia.getLink());
            }else {
                prepareAndSendMessage(chatId, "Соціальні мережі: " + socialMedia.getLink());
            }
        }
    }
    private void viewContacts(long chatId) {
        List<Addresses> addresses = (List<Addresses>) addressesRepository.findAll();
        for (Addresses address : addresses) {
            List<PhoneNumbers> phoneNumbers = phoneNumbersRepository.findByAddress_Id(address.getId());
            for (PhoneNumbers phoneNumber : phoneNumbers) {
                if (chatId == config.getOwnerId()) {
                    prepareAndSendMessage(chatId,
                            "Address ID: " + address.getId() + " Адреса: " + address.getAddress() +
                                   "Phone number ID: " + "Телефон: " + phoneNumber.getPhone());;

                } else {
                    prepareAndSendMessage(chatId,
                            "Адреса: "+"\n"+ address.getAddress() +"\n"+"Телефон: " + phoneNumber.getPhone());

                }
            }
        }
    }
}
