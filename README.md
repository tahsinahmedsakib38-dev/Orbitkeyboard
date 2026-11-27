# Orbit বাংলা কিবোর্ড
1. Android Studio (Koala+) এ `OrbitKeyboard` ফোল্ডার Open করুন, Gradle Sync হতে দিন।
2. Build > Build APK(s) → app-debug.apk ফোনে ইন্সটল করুন (Unknown sources চালু রেখে)।
3. অ্যাপ খুলে "কিবোর্ড চালু করুন" → Orbit বাংলা অন করুন, তারপর "কিবোর্ড সিলেক্ট করুন"।
ঐচ্ছিক: Mukto/Atma ফন্ট `app/src/main/assets/fonts/bn.ttf` নামে রাখলে বাংলা কী-তে ব্যবহার হবে।
কিবোর্ডে 🌐 চাপলে English ⇄ জাতীয় ক্লাসিক (বাংলা) বদলাবে; ⇧ ডাবল-ট্যাপে Caps Lock।

## কম্পিউটার ছাড়া (শুধু ফোনে) APK বানানো
GitHub-এ নতুন repository খুলে এই ফোল্ডারের সব ফাইল (লুকানো `.github` ফোল্ডারসহ) আপলোড করুন।
Actions ট্যাবে "Build APK" শেষ হলে Artifacts থেকে OrbitKeyboard-APK নামিয়ে ইন্সটল করুন।
