document.addEventListener("DOMContentLoaded", function () {
    const flash = document.querySelector(".flash:not([hidden]):not([data-persistent='true'])");
    if (flash) {
        setTimeout(function () {
            flash.style.transition = "opacity 0.35s ease";
            flash.style.opacity = "0";
            setTimeout(function () {
                flash.remove();
            }, 350);
        }, 2800);
    }

    const bulletinBody = document.getElementById("bulletinBody");
    const bulletinPreview = document.getElementById("newsletterPreview");
    if (bulletinBody && bulletinPreview) {
        const syncPreview = function () {
            const value = bulletinBody.value.trim();
            bulletinPreview.innerHTML = value
                ? value.replace(/\n/g, "<br>")
                : "Bulletin preview will appear here as the user types.";
        };

        bulletinBody.addEventListener("input", syncPreview);
        syncPreview();
    }

    const accountLockTimer = document.getElementById("accountLockTimer");
    if (accountLockTimer) {
        const loginSubmitButton = document.getElementById("loginSubmitButton");
        let remainingSeconds = Math.max(0, Number(accountLockTimer.dataset.remainingSeconds) || 0);
        if (loginSubmitButton) {
            loginSubmitButton.disabled = remainingSeconds > 0;
        }
        const syncLockTimer = function () {
            const minutes = Math.floor(remainingSeconds / 60);
            const seconds = remainingSeconds % 60;
            accountLockTimer.textContent =
                " Try again in " + String(minutes).padStart(2, "0") + ":" + String(seconds).padStart(2, "0") + ".";

            if (remainingSeconds <= 0) {
                accountLockTimer.textContent = " You can try signing in again now.";
                if (loginSubmitButton) {
                    loginSubmitButton.disabled = false;
                    loginSubmitButton.removeAttribute("disabled");
                }
                window.clearInterval(lockTimerInterval);
                return;
            }

            remainingSeconds -= 1;
        };
        const lockTimerInterval = window.setInterval(syncLockTimer, 1000);
        syncLockTimer();
    }

    const searchInput = document.getElementById("globalSearchInput");
    const searchSuggestions = document.getElementById("globalSearchSuggestions");
    let searchTimer = null;

    const clearSuggestions = function () {
        if (!searchSuggestions || !searchInput) {
            return;
        }
        searchSuggestions.innerHTML = "";
        searchSuggestions.classList.remove("is-visible");
        searchInput.setAttribute("aria-expanded", "false");
    };

    const addSuggestionGroup = function (label, items) {
        if (!items || items.length === 0) {
            return;
        }

        const group = document.createElement("div");
        group.className = "search-suggestions__group";

        const heading = document.createElement("p");
        heading.className = "search-suggestions__heading";
        heading.textContent = label;
        group.appendChild(heading);

        items.forEach(function (item) {
            const link = document.createElement("a");
            link.className = "search-suggestions__item";
            link.href = item.url;
            link.setAttribute("role", "option");

            const title = document.createElement("strong");
            title.textContent = item.title;

            const subtitle = document.createElement("span");
            subtitle.textContent = item.subtitle;

            link.appendChild(title);
            link.appendChild(subtitle);
            group.appendChild(link);
        });

        searchSuggestions.appendChild(group);
    };

    const renderSuggestions = function (data) {
        searchSuggestions.innerHTML = "";
        addSuggestionGroup("Communities", data.communities);
        addSuggestionGroup("Posts", data.posts);

        if (!searchSuggestions.innerHTML.trim()) {
            const empty = document.createElement("div");
            empty.className = "search-suggestions__empty";
            empty.textContent = "No matches yet";
            searchSuggestions.appendChild(empty);
        }

        searchSuggestions.classList.add("is-visible");
        searchInput.setAttribute("aria-expanded", "true");
    };

    if (searchInput && searchSuggestions) {
        searchInput.addEventListener("input", function () {
            const query = searchInput.value.trim();
            window.clearTimeout(searchTimer);

            if (query.length < 2) {
                clearSuggestions();
                return;
            }

            searchTimer = window.setTimeout(function () {
                fetch(CTX + "/search-suggestions?q=" + encodeURIComponent(query), {
                    headers: {
                        "Accept": "application/json"
                    }
                })
                    .then(function (response) {
                        return response.ok ? response.json() : { communities: [], posts: [] };
                    })
                    .then(renderSuggestions)
                    .catch(clearSuggestions);
            }, 180);
        });

        document.addEventListener("click", function (event) {
            if (!searchSuggestions.contains(event.target) && event.target !== searchInput) {
                clearSuggestions();
            }
        });

        searchInput.addEventListener("keydown", function (event) {
            if (event.key === "Escape") {
                clearSuggestions();
            }
        });
    }

    const postImageInput = document.getElementById("postImage");
    const postImageSizeWarning = document.getElementById("postImageSizeWarning");
    const maxPostImageSize = 5 * 1024 * 1024;

    const showPostImageWarning = function () {
        if (postImageSizeWarning) {
            postImageSizeWarning.textContent = "Keep the file below 5 MB.";
            postImageSizeWarning.hidden = false;
            postImageSizeWarning.style.opacity = "1";
        } else {
            window.alert("Keep the file below 5 MB.");
        }
    };

    const validatePostImageSize = function () {
        if (!postImageInput || !postImageInput.files || postImageInput.files.length === 0) {
            return true;
        }

        if (postImageInput.files[0].size > maxPostImageSize) {
            showPostImageWarning();
            postImageInput.value = "";
            return false;
        }

        if (postImageSizeWarning) {
            postImageSizeWarning.hidden = true;
            postImageSizeWarning.textContent = "";
        }
        return true;
    };

    if (postImageInput) {
        const postForm = postImageInput.closest("form");
        postImageInput.addEventListener("change", validatePostImageSize);

        if (postForm) {
            postForm.addEventListener("submit", function (event) {
                if (!validatePostImageSize()) {
                    event.preventDefault();
                }
            });
        }
    }

    const notificationCount = document.getElementById("notificationCount");
    if (notificationCount) {
        const syncNotificationCount = function () {
            fetch(CTX + "/notifications/count", {
                headers: {
                    "Accept": "application/json"
                }
            })
                .then(function (response) {
                    return response.ok ? response.json() : { count: 0 };
                })
                .then(function (data) {
                    const count = Number(data.count) || 0;
                    notificationCount.textContent = String(count);
                    notificationCount.hidden = count <= 0;
                })
                .catch(function () {
                    notificationCount.hidden = true;
                });
        };

        syncNotificationCount();
        window.setInterval(syncNotificationCount, 30000);
    }
});
