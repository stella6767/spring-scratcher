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
                <div id="qrContent" class="mt-4">
                    @template.component.qrContent(type = defaultType)
                </div>
                <!-- Step 2: Design QR Code -->
                <div class="mt-4">
                    <h2 class="font-semibold">2. Design your QR Code</h2>
                    <div class="flex space-x-2 mt-2">
                        <button class="p-2 border rounded bg-gray-300">🖼️ Frame</button>
                        <button class="p-2 border rounded">⬛ Shape</button>
                        <button class="p-2 border rounded">🏷️ Logo</button>
                    </div>

                    <!-- Frame Options -->
                    <div class="flex space-x-2 mt-4">
                        <div class="w-12 h-12 bg-gray-200 border rounded flex items-center justify-center">X</div>
                        <div class="w-12 h-12 bg-gray-200 border rounded"></div>
                        <div class="w-12 h-12 bg-gray-200 border rounded"></div>
                        <div class="w-12 h-12 bg-gray-200 border rounded"></div>
                    </div>
                </div>
            </div>

            <div class="w-1/4 flex-1 flex pl-6 flex-col items-center justify-center">
                <div class="w-full h-full bg-gray-300 flex items-center justify-center">
                    <p class="text-gray-500">[QR Code]</p>
                </div>

                <div class="mt-3 flex flex-col">
                    <button hx-post="/qrcode" hx-target="#response" hx-swap="none"
                            hx-include="#value, #type"
                            class="px-4 py-2 bg-gray-400 text-white rounded opacity-50 cursor-not-allowed">
                        Generate QR Code
                    </button>
                    <button class="mt-2 px-4 py-2 bg-gray-400 text-white rounded opacity-50 cursor-not-allowed">
                        ⬇ Download QR Code
                    </button>
                </div>
            </div>
        </div>


    </div>
`)

