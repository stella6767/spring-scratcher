@import freeapp.me.qrgenerator.config.QrGeneratorType
@import java.util.*

@param List<QrGeneratorType> types = freeapp.me.qrgenerator.config.QrGeneratorType.getEntries().stream().toList()
@param QrGeneratorType defaultType


<div class="flex flex-wrap space-x-4 space-y-2 border-b pb-2">
    @for(QrGeneratorType type : types)
        !{var selectedColor = (type == defaultType) ? "bg-green-100 text-green-700 tab-active" : "";}
        <div hx-get="/qr/${type.name()}"
             hx-target="#qrMainInput"
             hx-on--after-on-load="
                   let currentTab = document.querySelector('.tab-active');
                   currentTab.classList.remove('tab-active', 'bg-green-100', 'text-green-700')
                   let newTab = event.target
                   newTab.classList.add('tab-active', 'bg-green-100', 'text-green-700')
                "
             class="w-[calc(20%-1rem)] text-center px-4 py-2 border rounded cursor-pointer hover:bg-[#b4b4b480] ${selectedColor}">
            ${type.getIcon()} ${type.getFieldName()}
        </div>
    @endfor
</div>


