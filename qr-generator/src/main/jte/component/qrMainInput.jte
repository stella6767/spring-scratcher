@import freeapp.me.qrgenerator.config.QrGeneratorType
@param QrGeneratorType type

<div id="main-input-form">
    @if(type == QrGeneratorType.LINK)
        <h2 class="font-semibold">1. Complete the content</h2>
        <input id="value" name="text" type="text" placeholder="https://" class="w-full p-2 border rounded mt-2" required>
    @elseif(type == QrGeneratorType.VCARD)
        <h2 class="font-semibold">1. Complete the content</h2>
        <div class="flex">
            <input id="first-name" name="firstName" type="text" class="w-1/2 p-2 border rounded mt-2" required>
            <input id="last-name" name="lastName" type="text" class="w-1/2 p-2 border rounded mt-2" required>
        </div>
        <h2 class="font-semibold">Contact Information</h2>
        <div class="flex">
            <input id="Phone-number" name="phoneNumber" type="text" class="w-1/2 p-2 border rounded mt-2" required>
        </div>

    @else
        <div></div>
    @endif
</div>
