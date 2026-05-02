document.addEventListener("DOMContentLoaded", function () {
    const flash = document.querySelector(".flash");
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
