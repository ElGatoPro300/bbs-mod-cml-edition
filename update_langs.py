import json
import os
import glob

# -----------------------------------------------------------------------------
# Console Command: python update_langs.py
# -----------------------------------------------------------------------------

# Path to strings directory
strings_dir = r"C:\Users\PC\Documents\GitHub\bbs-mod\src\client\resources\assets\bbs\assets\strings"

# New keys and English values
new_keys_en = {
    "bbs.ui.film.layout.docked": "Docked layout",
    "bbs.ui.film.layout.horizontal": "Horizontal layout",
    "bbs.ui.film.layout.lock": "Lock layout",
    "bbs.ui.film.layout.main_bottom": "Main panel on bottom",
    "bbs.ui.film.layout.main_left": "Main panel on left",
    "bbs.ui.film.layout.main_right": "Main panel on right",
    "bbs.ui.film.layout.main_top": "Main panel on top",
    "bbs.ui.film.layout.vertical": "Vertical layout",
}

# -----------------------------------------------------------------------------
# Here you place the translations for your language,
# or you can tell an AI to create the translations
# for all languages ​​using the structure of this file.
# 
# This file also helps to organize translations with jumbled lines of code. Just remember,
# if you're going to use this file for only a few languages,
# delete the translation keys you won't be using to avoid unwanted translations.
# -----------------------------------------------------------------------------

# -----------------------------------------------------------------------------
# Layout Settings Keys
# -----------------------------------------------------------------------------

# Spanish (es_es)
new_keys_es = {
    "bbs.ui.film.layout.docked": "Diseño acoplado",
    "bbs.ui.film.layout.horizontal": "Diseño horizontal",
    "bbs.ui.film.layout.lock": "Bloquear diseño",
    "bbs.ui.film.layout.main_bottom": "Panel principal abajo",
    "bbs.ui.film.layout.main_left": "Panel principal a la izquierda",
    "bbs.ui.film.layout.main_right": "Panel principal a la derecha",
    "bbs.ui.film.layout.main_top": "Panel principal arriba",
    "bbs.ui.film.layout.vertical": "Diseño vertical"
}

# Portuguese (pt_br, pt_pt)
new_keys_pt = {
    "bbs.ui.film.layout.docked": "Layout acoplado",
    "bbs.ui.film.layout.horizontal": "Layout horizontal",
    "bbs.ui.film.layout.lock": "Bloquear layout",
    "bbs.ui.film.layout.main_bottom": "Painel principal na parte inferior",
    "bbs.ui.film.layout.main_left": "Painel principal na esquerda",
    "bbs.ui.film.layout.main_right": "Painel principal na direita",
    "bbs.ui.film.layout.main_top": "Painel principal no topo",
    "bbs.ui.film.layout.vertical": "Layout vertical"
}

# French (fr_fr)
new_keys_fr = {
    "bbs.ui.film.layout.docked": "Disposition ancrée",
    "bbs.ui.film.layout.horizontal": "Disposition horizontale",
    "bbs.ui.film.layout.lock": "Verrouiller la disposition",
    "bbs.ui.film.layout.main_bottom": "Panneau principal en bas",
    "bbs.ui.film.layout.main_left": "Panneau principal à gauche",
    "bbs.ui.film.layout.main_right": "Panneau principal à droite",
    "bbs.ui.film.layout.main_top": "Panneau principal en haut",
    "bbs.ui.film.layout.vertical": "Disposition verticale"
}

# German (de_de)
new_keys_de = {
    "bbs.ui.film.layout.docked": "Angedocktes Layout",
    "bbs.ui.film.layout.horizontal": "Horizontales Layout",
    "bbs.ui.film.layout.lock": "Layout sperren",
    "bbs.ui.film.layout.main_bottom": "Hauptpanel unten",
    "bbs.ui.film.layout.main_left": "Hauptpanel links",
    "bbs.ui.film.layout.main_right": "Hauptpanel rechts",
    "bbs.ui.film.layout.main_top": "Hauptpanel oben",
    "bbs.ui.film.layout.vertical": "Vertikales Layout"
}

# Russian (ru_ru)
new_keys_ru = {
    "bbs.ui.film.layout.docked": "Закрепленный макет",
    "bbs.ui.film.layout.horizontal": "Горизонтальный макет",
    "bbs.ui.film.layout.lock": "Заблокировать макет",
    "bbs.ui.film.layout.main_bottom": "Главная панель снизу",
    "bbs.ui.film.layout.main_left": "Главная панель слева",
    "bbs.ui.film.layout.main_right": "Главная панель справа",
    "bbs.ui.film.layout.main_top": "Главная панель сверху",
    "bbs.ui.film.layout.vertical": "Вертикальный макет"
}

# Simplified Chinese (zh_cn)
new_keys_cn = {
    "bbs.ui.film.layout.docked": "停靠布局",
    "bbs.ui.film.layout.horizontal": "水平布局",
    "bbs.ui.film.layout.lock": "锁定布局",
    "bbs.ui.film.layout.main_bottom": "主面板在底部",
    "bbs.ui.film.layout.main_left": "主面板在左侧",
    "bbs.ui.film.layout.main_right": "主面板在右侧",
    "bbs.ui.film.layout.main_top": "主面板在顶部",
    "bbs.ui.film.layout.vertical": "垂直布局"
}

# Polish (pl_pl)
new_keys_pl = {
    "bbs.ui.film.layout.docked": "Układ dokowany",
    "bbs.ui.film.layout.horizontal": "Układ poziomy",
    "bbs.ui.film.layout.lock": "Zablokuj układ",
    "bbs.ui.film.layout.main_bottom": "Główny panel na dole",
    "bbs.ui.film.layout.main_left": "Główny panel po lewej",
    "bbs.ui.film.layout.main_right": "Główny panel po prawej",
    "bbs.ui.film.layout.main_top": "Główny panel na górze",
    "bbs.ui.film.layout.vertical": "Układ pionowy"
}

# Turkish (tr_tr)
new_keys_tr = {
    "bbs.ui.film.layout.docked": "Yerleşik Düzen",
    "bbs.ui.film.layout.horizontal": "Yatay Düzen",
    "bbs.ui.film.layout.lock": "Düzeni Kilitle",
    "bbs.ui.film.layout.main_bottom": "Ana panel altta",
    "bbs.ui.film.layout.main_left": "Ana panel solda",
    "bbs.ui.film.layout.main_right": "Ana panel sağda",
    "bbs.ui.film.layout.main_top": "Ana panel üstte",
    "bbs.ui.film.layout.vertical": "Dikey Düzen"
}

# Korean (ko_kr)
new_keys_kr = {
    "bbs.ui.film.layout.docked": "도킹된 레이아웃",
    "bbs.ui.film.layout.horizontal": "수평 레이아웃",
    "bbs.ui.film.layout.lock": "레이아웃 잠금",
    "bbs.ui.film.layout.main_bottom": "하단 메인 패널",
    "bbs.ui.film.layout.main_left": "왼쪽 메인 패널",
    "bbs.ui.film.layout.main_right": "오른쪽 메인 패널",
    "bbs.ui.film.layout.main_top": "상단 메인 패널",
    "bbs.ui.film.layout.vertical": "수직 레이아웃"
}

# Vietnamese (vi_vn)
new_keys_vi = {
    "bbs.ui.film.layout.docked": "Bố cục neo",
    "bbs.ui.film.layout.horizontal": "Bố cục ngang",
    "bbs.ui.film.layout.lock": "Khóa bố cục",
    "bbs.ui.film.layout.main_bottom": "Bảng chính ở dưới",
    "bbs.ui.film.layout.main_left": "Bảng chính bên trái",
    "bbs.ui.film.layout.main_right": "Bảng chính bên phải",
    "bbs.ui.film.layout.main_top": "Bảng chính ở trên",
    "bbs.ui.film.layout.vertical": "Bố cục dọc"
}

# Ukrainian (uk_ua)
new_keys_uk = {
    "bbs.ui.film.layout.docked": "Закріплений макет",
    "bbs.ui.film.layout.horizontal": "Горизонтальний макет",
    "bbs.ui.film.layout.lock": "Заблокувати макет",
    "bbs.ui.film.layout.main_bottom": "Головна панель знизу",
    "bbs.ui.film.layout.main_left": "Головна панель зліва",
    "bbs.ui.film.layout.main_right": "Головна панель справа",
    "bbs.ui.film.layout.main_top": "Головна панель зверху",
    "bbs.ui.film.layout.vertical": "Вертикальний макет"
}

# Indonesian (id_id)
new_keys_id = {
    "bbs.ui.film.layout.docked": "Tata Letak Terpasang",
    "bbs.ui.film.layout.horizontal": "Tata Letak Horizontal",
    "bbs.ui.film.layout.lock": "Kunci Tata Letak",
    "bbs.ui.film.layout.main_bottom": "Panel utama di bawah",
    "bbs.ui.film.layout.main_left": "Panel utama di kiri",
    "bbs.ui.film.layout.main_right": "Panel utama di kanan",
    "bbs.ui.film.layout.main_top": "Panel utama di atas",
    "bbs.ui.film.layout.vertical": "Tata Letak Vertikal"
}

# Traditional Chinese (zh_tw)
new_keys_tw = {
    "bbs.ui.film.layout.docked": "停靠版面配置",
    "bbs.ui.film.layout.horizontal": "水平版面配置",
    "bbs.ui.film.layout.lock": "鎖定版面配置",
    "bbs.ui.film.layout.main_bottom": "主面板在底部",
    "bbs.ui.film.layout.main_left": "主面板在左側",
    "bbs.ui.film.layout.main_right": "主面板在右側",
    "bbs.ui.film.layout.main_top": "主面板在頂部",
    "bbs.ui.film.layout.vertical": "垂直版面配置"
}

# Arabic (ar_ar)
new_keys_ar = {
    "bbs.ui.film.layout.docked": "تخطيط مثبت",
    "bbs.ui.film.layout.horizontal": "تخطيط أفقي",
    "bbs.ui.film.layout.lock": "قفل التخطيط",
    "bbs.ui.film.layout.main_bottom": "اللوحة الرئيسية في الأسفل",
    "bbs.ui.film.layout.main_left": "اللوحة الرئيسية على اليسار",
    "bbs.ui.film.layout.main_right": "اللوحة الرئيسية على اليمين",
    "bbs.ui.film.layout.main_top": "اللوحة الرئيسية في الأعلى",
    "bbs.ui.film.layout.vertical": "تخطيط عمودي"
}

# Hungarian (hu_hu)
new_keys_hu = {
    "bbs.ui.film.layout.docked": "Dokkolt elrendezés",
    "bbs.ui.film.layout.horizontal": "Vízszintes elrendezés",
    "bbs.ui.film.layout.lock": "Elrendezés zárolása",
    "bbs.ui.film.layout.main_bottom": "Fő panel lent",
    "bbs.ui.film.layout.main_left": "Fő panel balra",
    "bbs.ui.film.layout.main_right": "Fő panel jobbra",
    "bbs.ui.film.layout.main_top": "Fő panel fent",
    "bbs.ui.film.layout.vertical": "Függőleges elrendezés"
}

# Thai (th_th)
new_keys_th = {
    "bbs.ui.film.layout.docked": "เค้าโครงแบบเชื่อมต่อ",
    "bbs.ui.film.layout.horizontal": "เค้าโครงแนวนอน",
    "bbs.ui.film.layout.lock": "ล็อกเค้าโครง",
    "bbs.ui.film.layout.main_bottom": "แผงหลักด้านล่าง",
    "bbs.ui.film.layout.main_left": "แผงหลักด้านซ้าย",
    "bbs.ui.film.layout.main_right": "แผงหลักด้านขวา",
    "bbs.ui.film.layout.main_top": "แผงหลักด้านบน",
    "bbs.ui.film.layout.vertical": "เค้าโครงแนวตั้ง"
}

# Urdu (ur_pk)
new_keys_ur = {
    "bbs.ui.film.layout.docked": "ڈاکڈ لے آؤٹ",
    "bbs.ui.film.layout.horizontal": "افقی ترتیب",
    "bbs.ui.film.layout.lock": "ترتیب کو لاک کریں",
    "bbs.ui.film.layout.main_bottom": "مین پینل نیچے",
    "bbs.ui.film.layout.main_left": "مین پینل بائیں طرف",
    "bbs.ui.film.layout.main_right": "مین پینل دائیں طرف",
    "bbs.ui.film.layout.main_top": "مین پینل اوپر",
    "bbs.ui.film.layout.vertical": "عمودی ترتیب"
}

# -----------------------------------------------------------------------------
# Logic to select dictionary
# -----------------------------------------------------------------------------

def update_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        filename = os.path.basename(filepath)
        
        # Select appropriate dictionary based on filename
        if filename == "es_es.json":
            updates = new_keys_es
        elif filename == "en_us.json":
            updates = new_keys_en
        # European
        elif filename == "fr_fr.json":
            updates = new_keys_fr
        elif filename == "de_de.json":
            updates = new_keys_de
        elif filename == "pt_br.json" or filename == "pt_pt.json":
            updates = new_keys_pt
        elif filename == "pl_pl.json":
            updates = new_keys_pl
        elif filename == "hu_hu.json":
            updates = new_keys_hu
        # Asian
        elif filename == "zh_cn.json":
            updates = new_keys_cn
        elif filename == "zh_tw.json":
            updates = new_keys_tw
        elif filename == "ko_kr.json":
            updates = new_keys_kr
        elif filename == "vi_vn.json":
            updates = new_keys_vi
        elif filename == "th_th.json":
            updates = new_keys_th
        elif filename == "id_id.json":
            updates = new_keys_id
        # Cyrillic
        elif filename == "ru_ru.json":
            updates = new_keys_ru
        elif filename == "uk_ua.json":
            updates = new_keys_uk
        # Middle Eastern / RTL
        elif filename == "tr_tr.json":
            updates = new_keys_tr
        elif filename == "ar_ar.json":
            updates = new_keys_ar
        elif filename == "ur_pk.json":
            updates = new_keys_ur
        else:
            # Fallback to English for any other unmapped files (e.g. he_il.json)
            updates = new_keys_en
            
        # Update data if key is missing
        modified = False
        for key, value in updates.items():
            if key not in data:
                data[key] = value
                modified = True
            elif filename == "en_us.json" and data[key] != value:
                 # Ensure English file matches exact values if they differ
                 data[key] = value
                 modified = True
        
        if modified or True: # Always write to ensure sorting
            # Sort keys to ensure correct order
            sorted_data = dict(sorted(data.items()))
            
            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(sorted_data, f, indent=4, ensure_ascii=False)
            print(f"Updated {filename}")
            
    except Exception as e:
        print(f"Error updating {filepath}: {e}")

# Process all json files
json_files = glob.glob(os.path.join(strings_dir, "*.json"))
for json_file in json_files:
    update_file(json_file)
