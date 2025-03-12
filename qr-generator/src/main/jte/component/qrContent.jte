
@import freeapp.me.qrgenerator.config.QrGeneratorType
@param QrGeneratorType type

@if(type == QrGeneratorType.LINK)
    <div>
        <h2 class="font-semibold">1. Complete the content</h2>
        <input id="value" type="text" placeholder="https://" class="w-full p-2 border rounded mt-2" required>
    </div>
@elseif(type == QrGeneratorType.SMS)
    <div>
        <h2 class="font-semibold">1. Complete the content</h2>
        <input id="value" type="text" placeholder="https://" class="w-full p-2 border rounded mt-2" required>
    </div>
@else
    <div></div>
@endif
