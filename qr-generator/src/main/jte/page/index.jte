@param freeapp.me.qrgenerator.config.QrGeneratorType defaultType = freeapp.me.qrgenerator.config.QrGeneratorType.LINK

@template.layout.main(content = @`
    <div class="">
        header
    </div>

    <div class="bg-gray-100 flex justify-center items-center min-h-screen">

        <div class="flex bg-white p-6 rounded-lg shadow-md w-[900px]">

            <div class="w-3/4">
                <!-- Tabs -->
                @template.component.typeTabs(defaultType = defaultType)
                <!-- Step 1: Complete Content -->
                <div id="qrMainInput" class="mt-4">
                    @template.component.qrMainInput(type = defaultType)
                </div>
                <!-- Step 2: Design QR Code -->
                <div class="mt-4">
                    @template.component.qrDesign()
                </div>
            </div>

            <div class="w-1/4 flex-1 flex pl-6 flex-col items-center justify-center">
                <div class="w-full h-full bg-gray-300 flex items-center justify-center">
                    <p class="text-gray-500">[QR Code]</p>
                </div>

                @template.component.qrCodeBtn(type = defaultType)

            </div>
        </div>


    </div>
`)

