const darkButton = document.getElementById("DarkButton");
const shortenButton = document.getElementById("ShortenButton");
const urlInput = document.getElementById("urlInput");
const shortUrl = document.getElementById("shortenurl");
const copyButton = document.getElementById("copyingbutton");


// Dark mode
darkButton.addEventListener("click", () => {
    document.body.classList.toggle("dark");
});


// Shorten URL
shortenButton.addEventListener("click", async () => {
    const originalUrl = urlInput.value.trim();

    if (!originalUrl) {
        alert("Please enter a URL.");
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/shorten", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                originalUrl: originalUrl
            })
        });

        if (!response.ok) {
            throw new Error("Failed to shorten URL");
        }

        const data = await response.json();

        shortUrl.textContent = data.shortUrl;

    } catch (error) {
        console.error(error);
        alert("Could not shorten the URL.");
    }
});


// Copy shortened URL
copyButton.addEventListener("click", async () => {
    const url = shortUrl.textContent;

    if (!url || url === "Short Url") {
        alert("There is no shortened URL to copy.");
        return;
    }

    try {
        await navigator.clipboard.writeText(url);
        alert("Short URL copied!");
    } catch (error) {
        console.error(error);
        alert("Could not copy the URL.");
    }
});