@import freeapp.me.qrgenerator.config.QrGeneratorType
@param QrGeneratorType type
@param String imageData = ""

@param Boolean isGenerated = false




<div id="qr-code-btn" class="mt-3 flex flex-col"
     hx-on--load="initInputField();"
     hx-swap-oob="true"
>
    @if(isGenerated)
        <button
                id="qr-generate-btn"
                class="px-4 py-2 bg-gray-400 text-white border rounded opacity-50 cursor-not-allowed">
            Generate QR Code
        </button>
        <button id="qr-download-btn"
                class="mt-2 px-4 py-2 bg-white-400 text-blue rounded opacity-50 cursor-pointer hover:bg-[#b4b4b480]">
            <a download="qrcode.png" href="data:image/png;base64,${imageData}">
                ⬇ Download QR Code
            </a>
        </button>
    @else
        <button
                hx-encoding="multipart/form-data"
                id="qr-generate-btn"
                hx-post="/qrcode?type=${type.name()}"
                hx-target="#qr-container"
                hx-include="#main-input-form"
                hx-indicator="#spinner"
                class="px-4 py-2 bg-white-400 text-blue border rounded hover:bg-[#b4b4b480] opacity-50 cursor-pointer">
            Generate QR Code
        </button>
        <button id="qr-download-btn"
                class="mt-2 px-4 py-2 bg-gray-400 text-white rounded opacity-50 cursor-not-allowed">
            ⬇ Download QR Code
        </button>
    @endif
</div>


