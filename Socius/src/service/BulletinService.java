package service;

import util.ValidationUtil;

public class BulletinService {

    public boolean isBulletinValid(String subject, String body) {
        return ValidationUtil.hasLengthBetween(subject, 3, 300)
            && ValidationUtil.hasLengthBetween(body, 20, 10000);
    }

    public String toPreview(String body) {
        return body == null ? "" : body.replace("\n", "<br/>");
    }
}
