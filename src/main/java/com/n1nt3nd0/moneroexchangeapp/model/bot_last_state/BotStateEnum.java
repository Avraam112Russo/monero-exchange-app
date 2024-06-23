package com.n1nt3nd0.moneroexchangeapp.model.bot_last_state;

import java.io.Serializable;

public enum BotStateEnum implements Serializable {
    START_COMMAND,
    BUY_XMR_COMMAND,
    USER_TYPE_AMOUNT_XMR_WANT_BUY,
    USER_CHOOSE_SBERBANK_PAYMENT,
    USER_TYPE_XMR_ADDRESS,
    USER_CREATED_NEW_XMR_EXCHANGE_ORDER,
    USER_MADE_PAYMENT,
    ADMIN_CONFIRMED_XMR_EXCHANGE_ORDER,
    ORDER_SUCCESSFULLY_COMPLETE
}
