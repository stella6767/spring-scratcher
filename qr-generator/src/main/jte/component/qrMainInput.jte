@import freeapp.me.qrgenerator.config.QrGeneratorType
@param QrGeneratorType type

<div id="main-input-form">
    @if(type == QrGeneratorType.LINK)
        <h2 class="font-semibold">1. Complete the content</h2>
        <input id="value" name="text" type="text" placeholder="https://"
               class="w-full p-2 border rounded mt-2" required>
    @elseif(type == QrGeneratorType.TEXT)
        <h2 class="font-semibold">1. Complete the content</h2>
        <div class="flex flex-col mt-2">
            <label for="generator-section-input-2" class="font-semibold">Message</label>
            <textarea id="value" name="text" type="text" maxlength="1000"
                      placeholder="Write your text here" class="border p-2 mt-2"
                      required></textarea>
        </div>
    @elseif(type == QrGeneratorType.TEL)
        <h2 class="font-semibold">1. Complete the content</h2>
        <div class="flex space-x-3 mt-2">
            <div class="w-1/4">
                <label class="font-semibold">Country code</label>
                @template.component.countryCode()
            </div>

            <div class="w-3/4 flex flex-col">
                <label class="font-semibold">Phone number</label>
                <input id="phone-number" name="phoneNumber" type="text" class="p-2 border rounded mt-2" required>
            </div>

        </div>
    @elseif(type == QrGeneratorType.WIFI)
        <h2 class="font-semibold">1. Complete the content</h2>
        <div class="flex space-x-3 mt-2">
            <div class="flex flex-col flex-1">
                <label class="font-semibold">Network name</label>
                <input id="ssid" name="ssid" placeholder="SSID" type="text" class="p-2 border rounded mt-2" required>
            </div>
            <div class="flex flex-col flex-1">
                <label class="font-semibold">Type of encryption</label>
                <select name="encryption" id="encryption" class="p-2 border rounded mt-2">
                    <option value="">Type of encryption</option>
                    <option value="WEP">WEP</option>
                    <option value="WPA">WPA/WPA2</option>
                </select>
            </div>
            <div class="flex flex-col flex-1">
                <label class="font-semibold">Password</label>
                <input id="wifi-password" name="password" placeholder="WI-FI password" type="text"
                       class="p-2 border rounded mt-2" required>
            </div>
        </div>
    @elseif(type == QrGeneratorType.VCARD)
        <h2 class="font-semibold">1. Complete the content</h2>
        <div class="flex space-x-3 mt-2">
            <div class="flex flex-col flex-1">
                <label class="font-semibold">first name</label>
                <input id="first-name" name="firstName" type="text" class="p-2 border rounded mt-2" required>
            </div>
            <div class="flex flex-col flex-1">
                <label class="font-semibold">last name</label>
                <input id="last-name" name="lastName" type="text" class="p-2 border rounded mt-2" required>
            </div>
        </div>
        <br/>
        <h2 class="font-semibold">Contact Information</h2>
        <div class="flex flex-col">
            <label class="font-semibold">Phone number</label>
            <input id="Phone-number" name="phoneNumber" type="text" class="p-2 border rounded mt-2" required>
        </div>

    @elseif(type == QrGeneratorType.PDF)
        <h2 class="font-semibold">PDF QR Code</h2>
        <p class="font-light">Share PDF Document inside your QR Code.</p>
        <button
                onclick="var input = document.getElementById('fileInput')
                   input.click(); input.classList.remove('hidden')  "
                class="mt-2 w-1/2 border rounded p-2 h-[50px] cursor-pointer hover:bg-[#b4b4b480]" >
            Upload Now
        </button>
        <input type="file" id="fileInput" name="file"
               class="hidden">

    @else
        <div></div>
    @endif
</div>
