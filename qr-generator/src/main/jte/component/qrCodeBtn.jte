@import freeapp.me.qrgenerator.config.QrGeneratorType
@param QrGeneratorType type


<div id="qr-code-btn" class="mt-3 flex flex-col" hx-swap-oob="true">
    <button hx-post="/qrcode?type=${type.name()}"  hx-swap="none"
            hx-include="#main-input-form"
            class="px-4 py-2 bg-gray-400 text-white rounded opacity-50 cursor-not-allowed">
        Generate QR Code
    </button>
    <button class="mt-2 px-4 py-2 bg-gray-400 text-white rounded opacity-50 cursor-not-allowed">
        ⬇ Download QR Code
    </button>
</div>
