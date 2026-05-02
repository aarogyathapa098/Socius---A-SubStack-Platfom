package service;

import model.Community;
import util.SlugUtil;
import util.ValidationUtil;

public class CommunityService {

    public boolean isCommunityValid(Community community) {
        return community != null
            && ValidationUtil.hasLengthBetween(community.getName(), 3, 100)
            && ValidationUtil.hasLengthBetween(community.getDescription(), 10, 500)
            && ValidationUtil.isPresent(community.getGuidelines());
    }

    public void prepareForSave(Community community) {
        if (community != null && !ValidationUtil.isPresent(community.getSlug())) {
            community.setSlug(SlugUtil.toSlug(community.getName()));
        }
    }
}
