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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
    private final UserRepository userRepository;
    private final PriceListRepository priceListRepository;
    private final PromoRepository promoRepository;
    private final AddressesRepository addressesRepository;
    private final PhoneNumbersRepository phoneNumbersRepository;
    private final SocialMediaRepository socialMediaRepository;

    final BotConfig config;
    @Value("${hello.text}")
    String HELLO_TEXT;
    @Value("${help.text}")
    String HELP_TEXT;
    @Value("${command}")
    String COMMAND;

    @Value("${owner.help.text}")
    String OWNER_HELP_TEXT;
    @Value("${price.help}")
    String PRICE_HELP;
    @Value("${contact.help}")
    String CONTACT_HELP;
    @Value("${phone.num.help}")
    String PHONE_NUM_HELP;
    @Value("${social.media.help}")
    String SOCIAL_MEDIA_HELP;
    @Value("${promo.help}")
    String PROMO_HELP;
    @Value("${send.help}")
    String SEND_HELP;

    @Value("${keyboard1}")
    String KEYBOARD_1;
    @Value("${keyboard2}")
    String KEYBOARD_2;
    @Value("${instruction.keyboard1}")
    String INSTRUCTION_KEYBOARD_1;
    @Value("${instruction.keyboard2}")
    String INSTRUCTION_KEYBOARD_2;


    public TelegramBot(BotConfig config, UserRepository userRepository, PriceListRepository priceListRepository, PromoRepository promoRepository, AddressesRepository addressesRepository, PhoneNumbersRepository phoneNumbersRepository, SocialMediaRepository socialMediaRepository) {
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
        this.userRepository = userRepository;
        this.priceListRepository = priceListRepository;
        this.promoRepository = promoRepository;
        this.addressesRepository = addressesRepository;
        this.phoneNumbersRepository = phoneNumbersRepository;
        this.socialMediaRepository = socialMediaRepository;
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
                ownerCommand(messageText);
            } else {
                userCommand(update, messageText, chatId);
            }
        }
    }

    private void ownerCommand(String messageText) {
        if (messageText.contains("/send")) {
            send(messageText);
        }

        if (messageText.contains("/send_to_id")) {
            sendToId(messageText);
        }

        if (messageText.contains("/add_promo")) {
            addPromo(messageText);
        }

        if (messageText.contains("/delete_promo")) {
            deletePromo(messageText);
        }
        if (messageText.contains("/add_price")) {
            addPrice(messageText);
        }

        if (messageText.contains("/delete_price")) {
            deletePrice(messageText);
        }

        if (messageText.contains("/add_contacts")) {
            addContacts(messageText);
        }

        if (messageText.contains("/delete_address")) {
            deleteAddress(messageText);
        }

        if (messageText.contains("/add_phone")) {
            addPhone(messageText);
        }

        if (messageText.contains("/delete_phone")) {
            deletePhone(messageText);
        }

        if (messageText.contains("/add_social")) {
            addSocial(messageText);
        }

        if (messageText.contains("/delete_social")) {
            deleteSocial(messageText);
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

    }

    private void userCommand(Update update, String messageText, long chatId) {
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
            case "Записатись на масаж":
                prepareAndSendMessage(config.getOwnerId(), messageText);
                prepareAndSendMessage(chatId, INSTRUCTION_KEYBOARD_1);
                break;
            case "Поставити запитання":
                prepareAndSendMessage(config.getOwnerId(), messageText);
                prepareAndSendMessage(chatId, INSTRUCTION_KEYBOARD_2);
                break;
        }
        if (messageText.contains("0")) {
            prepareAndSendMessage(config.getOwnerId(), "Повідомлення надійшло від користувача: " +
                    update.getMessage().getChat().getFirstName() + "\n\n" + "ID користувача: " + chatId + "\n\n" + messageText);

        }
    }

    private void deleteSocial(String messageText) {
        String idStr = messageText.replaceAll("[^0-9.]", "").trim();
        try {
            long id = Long.parseLong(idStr);
            socialMediaRepository.deleteById(id);
        } catch (NumberFormatException e) {
            prepareAndSendMessage(config.getOwnerId(),
                    "Помилка, невірний формат повідомлення " + e.getMessage());
        }
    }

    private void addSocial(String messageText) {
        var addSocial = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
        SocialMedia socialMedia = new SocialMedia();
        socialMedia.setLink(addSocial);
        socialMediaRepository.save(socialMedia);
    }

    private void deletePhone(String messageText) {
        String idStr = messageText.replaceAll("[^0-9.]", "").trim();
        try {
            long id = Long.parseLong(idStr);
            phoneNumbersRepository.deleteById(id);
        } catch (NumberFormatException e) {
            prepareAndSendMessage(config.getOwnerId(),
                    "Помилка, невірний формат повідомлення " + e.getMessage());
        }
    }

    private void addPhone(String messageText) {
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

    private void deleteAddress(String messageText) {
        String idStr = messageText.replaceAll("[^0-9.]", "").trim();
        try {
            long id = Long.parseLong(idStr);
            addressesRepository.deleteById(id);
        } catch (NumberFormatException e) {
            prepareAndSendMessage(config.getOwnerId(),
                    "Помилка, невірний формат повідомлення " + e.getMessage());
        }
    }

    private void addContacts(String messageText) {
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

    private void deletePrice(String messageText) {
        String idStr = messageText.replaceAll("[^0-9.]", "").trim();
        try {
            long id = Long.parseLong(idStr);
            priceListRepository.deleteById(id);
        } catch (NumberFormatException e) {
            prepareAndSendMessage(config.getOwnerId(),
                    "Помилка, невірний формат повідомлення " + e.getMessage());
        }
    }

    private void addPrice(String messageText) {
        var addPrice = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
        PriceList priceList = new PriceList();
        priceList.setPrice(addPrice);
        priceListRepository.save(priceList);
    }

    private void deletePromo(String messageText) {
        String idStr = messageText.replaceAll("[^0-9.]", "").trim();
        try {
            long id = Long.parseLong(idStr);
            promoRepository.deleteById(id);
        } catch (NumberFormatException e) {
            prepareAndSendMessage(config.getOwnerId(),
                    "Помилка, невірний формат повідомлення " + e.getMessage());
        }
    }

    private void addPromo(String messageText) {
        var addPromo = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
        Promo promo = new Promo();
        promo.setPromo(addPromo);
        promoRepository.save(promo);
    }

    private void sendToId(String messageText) {
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

    private void send(String messageText) {
        var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
        var users = userRepository.findAll();
        for (User user : users) {
            prepareAndSendMessage(user.getChatId(), textToSend);
        }
    }


    private void startCommentReceived(long chatId) {
        sendMessage(chatId, HELLO_TEXT + "\n\n" + COMMAND);
    }

    private void registerUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
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

    private void sendMessage(long chatId, String textToSend) {
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

    private void viewPrice(long chatId) {
        List<PriceList> priceLists = (List<PriceList>) priceListRepository.findAll();
        for (PriceList priceList : priceLists) {
            if (chatId == config.getOwnerId()) {
                prepareAndSendMessage(chatId, "ID: " + priceList.getId() + " Price: " + priceList.getPrice());
            } else {
                prepareAndSendMessage(chatId, priceList.getPrice());
            }
        }
    }

    private void viewPromo(long chatId) {
        List<Promo> promos = (List<Promo>) promoRepository.findAll();
        for (Promo promo : promos) {
            if (chatId == config.getOwnerId()) {
                prepareAndSendMessage(chatId, "ID: " + promo.getId() + " Promo: " + promo.getPromo());
            } else {
                prepareAndSendMessage(chatId, promo.getPromo());
            }
        }
    }

    private void viewAddress(long chatId) {
        List<Addresses> addresses = (List<Addresses>) addressesRepository.findAll();
        for (Addresses address : addresses) {
            prepareAndSendMessage(chatId,
                    "ID: " + address.getId() + "Наш кабінет знаходеться за адресою: " + address.getAddress());
        }
    }

    private void viewPhone(long chatId) {
        List<PhoneNumbers> phoneNumbers = (List<PhoneNumbers>) phoneNumbersRepository.findAll();
        for (PhoneNumbers phoneNumber : phoneNumbers) {
            prepareAndSendMessage(chatId,
                    "ID: " + phoneNumber.getId() + "Номер телефону: " + phoneNumber.getPhone());
        }
    }

    private void viewSocialMedia(long chatId) {
        List<SocialMedia> socialMedias = (List<SocialMedia>) socialMediaRepository.findAll();
        for (SocialMedia socialMedia : socialMedias) {
            if (chatId == config.getOwnerId()) {
                prepareAndSendMessage(chatId,
                        "ID: " + socialMedia.getId() + " Соціальні мережі: " + socialMedia.getLink());
            } else {
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
                                    "Phone number ID: " + "Телефон: " + phoneNumber.getPhone());
                    ;

                } else {
                    prepareAndSendMessage(chatId,
                            "Адреса: " + "\n" + address.getAddress() + "\n" + "Телефон: " + phoneNumber.getPhone());

                }
            }
        }
    }
}
