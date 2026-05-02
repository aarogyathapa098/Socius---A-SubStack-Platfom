<%@ include file="../common/header.jsp" %>
<main class="content-shell content-shell--public">
    <section class="section-card section-card--hero-copy">
        <p class="eyebrow">Contact & Support</p>
        <h1>Need help with access, moderation, or account recovery?</h1>
        <p class="lead-sm">Use this page for account help, community support, moderation questions, safety concerns, or anything that needs a human response from the Socius team.</p>
    </section>

    <section class="split-section">
        <div class="section-card">
            <form class="stack-form">
                <label for="supportName">Full name</label>
                <input id="supportName" type="text" placeholder="Your name">

                <label for="supportEmail">Email address</label>
                <input id="supportEmail" type="email" placeholder="name@example.com">

                <label for="supportTopic">Topic</label>
                <select id="supportTopic">
                    <option>Account recovery</option>
                    <option>Community support</option>
                    <option>Reporting concern</option>
                    <option>General enquiry</option>
                </select>

                <label for="supportMessage">Message</label>
                <textarea id="supportMessage" rows="6" placeholder="Describe your request clearly."></textarea>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Send request</button>
                </div>
            </form>
        </div>

        <div class="section-card">
            <p class="eyebrow">Direct Channels</p>
            <ul class="bullet-list">
                <li>Email: support@socius.app</li>
                <li>Hours: Monday to Friday, 09:00 to 17:00</li>
                <li>Escalation: moderation@socius.app</li>
                <li>Location: Socius support desk</li>
            </ul>
        </div>
    </section>
</main>
<%@ include file="../common/footer.jsp" %>
