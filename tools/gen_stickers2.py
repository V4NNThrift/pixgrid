import cairosvg, os
import math

os.makedirs('stickers/set2', exist_ok=True)
S = 200

def svg(body, defs=""):
    return f'''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {S} {S}" width="{S}" height="{S}">
<defs>{defs}</defs>
{body}
</svg>'''

def grad(gid, c1, c2, x1=0, y1=0, x2=1, y2=1):
    return f'<linearGradient id="{gid}" x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}"><stop offset="0" stop-color="{c1}"/><stop offset="1" stop-color="{c2}"/></linearGradient>'

def radial(gid, c1, c2):
    return f'<radialGradient id="{gid}" cx="0.4" cy="0.35" r="0.75"><stop offset="0" stop-color="{c1}"/><stop offset="1" stop-color="{c2}"/></radialGradient>'

S2 = {}

# --- Set 2: gaya outline tebal / doodle ---
S2['circle_out'] = svg(
    '<circle cx="100" cy="100" r="72" fill="none" stroke="url(#g)" stroke-width="14" stroke-dasharray="18 12"/>',
    grad('g','#FF7A45','#FFB347'))

S2['square_out'] = svg(
    '<rect x="34" y="34" width="132" height="132" rx="26" fill="none" stroke="url(#g)" stroke-width="14"/>',
    grad('g','#8A7CFF','#4FC3F7'))

S2['plus'] = svg(
    '<path d="M86 34 H114 V86 H166 V114 H114 V166 H86 V114 H34 V86 H86 Z" fill="url(#g)" stroke="#fff" stroke-width="5" stroke-linejoin="round"/>',
    grad('g','#FF5A5A','#FFB347'))

S2['minus'] = svg(
    '<rect x="30" y="84" width="140" height="32" rx="16" fill="url(#g)" stroke="#fff" stroke-width="4"/>',
    grad('g','#7FD8FF','#2F80ED'))

S2['arrow_right'] = svg(
    '<path d="M26 100 H140 M140 100 L104 66 M140 100 L104 134" fill="none" stroke="url(#g)" stroke-width="16" stroke-linecap="round" stroke-linejoin="round"/>',
    grad('g','#FF7A45','#FFD54A'))

S2['arrow_up'] = svg(
    '<path d="M100 174 V60 M100 60 L66 96 M100 60 L134 96" fill="none" stroke="url(#g)" stroke-width="16" stroke-linecap="round" stroke-linejoin="round"/>',
    grad('g','#5ED69A','#1F9D62'))

S2['smile'] = svg(
    '<circle cx="100" cy="100" r="72" fill="url(#g)" stroke="#fff" stroke-width="6"/>'
    '<circle cx="76" cy="86" r="9" fill="#2A2A3A"/>'
    '<circle cx="124" cy="86" r="9" fill="#2A2A3A"/>'
    '<path d="M66 118 Q100 152 134 118" fill="none" stroke="#2A2A3A" stroke-width="10" stroke-linecap="round"/>',
    grad('g','#FFE07A','#FFB347'))

S2['wink'] = svg(
    '<circle cx="100" cy="100" r="72" fill="url(#g)" stroke="#fff" stroke-width="6"/>'
    '<circle cx="76" cy="86" r="9" fill="#2A2A3A"/>'
    '<path d="M112 84 Q124 76 136 86" fill="none" stroke="#2A2A3A" stroke-width="8" stroke-linecap="round"/>'
    '<path d="M66 118 Q100 148 134 118" fill="none" stroke="#2A2A3A" stroke-width="10" stroke-linecap="round"/>',
    grad('g','#FFB3D9','#FF6FA8'))

S2['sad'] = svg(
    '<circle cx="100" cy="100" r="72" fill="url(#g)" stroke="#fff" stroke-width="6"/>'
    '<circle cx="76" cy="86" r="9" fill="#2A2A3A"/>'
    '<circle cx="124" cy="86" r="9" fill="#2A2A3A"/>'
    '<path d="M66 138 Q100 106 134 138" fill="none" stroke="#2A2A3A" stroke-width="10" stroke-linecap="round"/>',
    grad('g','#A0C0E0','#5A7AA0'))

S2['surprise'] = svg(
    '<circle cx="100" cy="100" r="72" fill="url(#g)" stroke="#fff" stroke-width="6"/>'
    '<circle cx="76" cy="84" r="11" fill="#2A2A3A"/>'
    '<circle cx="124" cy="84" r="11" fill="#2A2A3A"/>'
    '<ellipse cx="100" cy="126" rx="16" ry="20" fill="#2A2A3A"/>',
    grad('g','#B39DFF','#6C4CE0'))

S2['heart_out'] = svg(
    '<path d="M100 170 C40 128 22 100 22 76 C22 48 44 32 66 32 C84 32 94 44 100 56 C106 44 116 32 134 32 C156 32 178 48 178 76 C178 100 160 128 100 170 Z" fill="none" stroke="url(#g)" stroke-width="14"/>',
    grad('g','#FF5A5A','#FF8FB1'))

S2['star_out'] = svg(
    '<polygon points="100,26 124,78 180,86 138,124 150,180 100,152 50,180 62,124 20,86 76,78" fill="none" stroke="url(#g)" stroke-width="12" stroke-linejoin="round"/>',
    grad('g','#FFD54A','#FF9E1B'))

S2['crown_out'] = svg(
    '<path d="M32 148 L28 72 L66 104 L100 50 L134 104 L172 72 L168 148 Z" fill="none" stroke="url(#g)" stroke-width="12" stroke-linejoin="round"/>',
    grad('g','#FFE07A','#E8A400'))

S2['sun'] = svg(
    '<circle cx="100" cy="100" r="44" fill="url(#g)"/>' +
    ''.join(f'<line x1="100" y1="18" x2="100" y2="40" stroke="url(#g)" stroke-width="10" stroke-linecap="round" transform="rotate({a} 100 100)"/>' for a in range(0,360,45)),
    grad('g','#FFE07A','#FF9E1B'))

S2['moon'] = svg(
    '<path d="M126 30 A74 74 0 1 0 126 170 A58 58 0 1 1 126 30 Z" fill="url(#g)" stroke="#fff" stroke-width="5"/>',
    grad('g','#E8E8FF','#A0A0D0'))

S2['rainbow'] = svg(
    ''.join(f'<path d="M28 152 A72 72 0 0 1 172 152" fill="none" stroke="{c}" stroke-width="12" transform="translate(0 {i*14})"/>'
            for i,c in enumerate(['#FF5A5A','#FFB347','#FFE07A','#5ED69A','#4FC3F7','#8A7CFF'])),
)

S2['music'] = svg(
    '<circle cx="74" cy="142" r="24" fill="url(#g)"/>'
    '<circle cx="140" cy="126" r="20" fill="url(#g)"/>'
    '<path d="M94 142 V52 L160 36 V126" fill="none" stroke="url(#g)" stroke-width="12" stroke-linejoin="round"/>',
    grad('g','#FF7A45','#E8447A'))

S2['camera'] = svg(
    '<rect x="26" y="58" width="148" height="106" rx="18" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<circle cx="100" cy="112" r="34" fill="#1A1A22"/>'
    '<circle cx="100" cy="112" r="22" fill="#4A4A5A"/>'
    '<rect x="76" y="42" width="48" height="20" rx="8" fill="url(#g)"/>',
    grad('g','#8A7CFF','#4FC3F7'))

S2['pin'] = svg(
    '<path d="M100 24 C66 24 40 50 40 84 C40 130 100 178 100 178 C100 178 160 130 160 84 C160 50 134 24 100 24 Z" fill="url(#g)" stroke="#fff" stroke-width="6"/>'
    '<circle cx="100" cy="84" r="22" fill="#fff"/>',
    grad('g','#FF5A5A','#E8447A'))

S2['gift'] = svg(
    '<rect x="34" y="86" width="132" height="86" rx="10" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<rect x="26" y="66" width="148" height="28" rx="8" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<rect x="88" y="66" width="24" height="106" fill="#fff" opacity="0.75"/>'
    '<path d="M100 66 C76 66 64 48 76 40 C88 32 98 50 100 66 Z" fill="#fff" opacity="0.85"/>'
    '<path d="M100 66 C124 66 136 48 124 40 C112 32 102 50 100 66 Z" fill="#fff" opacity="0.85"/>',
    grad('g','#FF7A45','#FFB347'))

S2['cake'] = svg(
    '<rect x="34" y="96" width="132" height="64" rx="12" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<path d="M34 112 Q52 96 70 112 Q88 128 106 112 Q124 96 142 112 Q160 128 166 112" fill="none" stroke="#fff" stroke-width="6"/>'
    '<rect x="94" y="52" width="12" height="32" rx="6" fill="#fff"/>'
    '<ellipse cx="100" cy="48" rx="9" ry="13" fill="#FFD54A"/>',
    grad('g','#FFB3D9','#FF6FA8'))

S2['coffee'] = svg(
    '<path d="M38 70 H146 V126 C146 152 124 170 92 170 C60 170 38 152 38 126 Z" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<path d="M146 90 H162 C176 90 184 102 184 114 C184 126 176 138 162 138 H146" fill="none" stroke="url(#g)" stroke-width="10"/>'
    '<path d="M66 52 Q74 38 66 24 M92 52 Q100 38 92 24 M118 52 Q126 38 118 24" fill="none" stroke="#fff" stroke-width="6" stroke-linecap="round" opacity="0.8"/>',
    grad('g','#B07A50','#7A4A28'))

S2['pizza'] = svg(
    '<path d="M100 26 L176 166 Q100 190 24 166 Z" fill="url(#g)" stroke="#fff" stroke-width="6" stroke-linejoin="round"/>'
    '<circle cx="76" cy="118" r="11" fill="#C04030"/>'
    '<circle cx="124" cy="130" r="11" fill="#C04030"/>'
    '<circle cx="100" cy="92" r="9" fill="#C04030"/>',
    grad('g','#FFD54A','#FF9E1B'))

S2['planet'] = svg(
    '<circle cx="100" cy="100" r="54" fill="url(#g)"/>'
    '<ellipse cx="100" cy="100" rx="86" ry="24" fill="none" stroke="#fff" stroke-width="8" opacity="0.85" transform="rotate(-22 100 100)"/>',
    radial('g','#8FD0FF','#20407A'))

S2['rocket'] = svg(
    '<path d="M100 20 C124 46 138 84 138 118 L100 156 L62 118 C62 84 76 46 100 20 Z" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<circle cx="100" cy="86" r="18" fill="#fff" opacity="0.9"/>'
    '<path d="M62 118 L40 156 L66 148 M138 118 L160 156 L134 148" fill="url(#g2)" stroke="#fff" stroke-width="5"/>'
    '<path d="M100 156 L88 184 L100 176 L112 184 Z" fill="#FFD54A"/>',
    grad('g','#E0E8F5','#A0A8C0') + grad('g2','#FF7A45','#E8447A'))

S2['bulb'] = svg(
    '<path d="M100 24 C70 24 48 48 48 78 C48 100 62 114 72 128 L72 142 L128 142 L128 128 C138 114 152 100 152 78 C152 48 130 24 100 24 Z" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<rect x="76" y="148" width="48" height="12" rx="6" fill="#A0A0B0"/>'
    '<rect x="80" y="164" width="40" height="12" rx="6" fill="#A0A0B0"/>',
    grad('g','#FFE07A','#FFB347'))

S2['bulb_on'] = svg(
    '<path d="M100 24 C70 24 48 48 48 78 C48 100 62 114 72 128 L72 142 L128 142 L128 128 C138 114 152 100 152 78 C152 48 130 24 100 24 Z" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<path d="M84 74 Q100 58 116 74 M100 66 V108" fill="none" stroke="#FFF8C0" stroke-width="7" stroke-linecap="round"/>'
    '<rect x="76" y="148" width="48" height="12" rx="6" fill="#A0A0B0"/>'
    '<rect x="80" y="164" width="40" height="12" rx="6" fill="#A0A0B0"/>',
    grad('g','#FF5A5A','#FF9E1B'))

S2['diamond'] = svg(
    '<path d="M60 36 H140 L172 80 L100 172 L28 80 Z" fill="url(#g)" stroke="#fff" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M28 80 H172 M60 36 L86 80 L100 172 M140 36 L114 80 L100 172" fill="none" stroke="#fff" stroke-width="3" opacity="0.5"/>',
    grad('g','#8FD0FF','#4A80D0'))

S2['spade'] = svg(
    '<path d="M100 28 C100 28 44 82 44 118 C44 142 62 158 84 158 C92 158 98 154 100 150 C102 154 108 158 116 158 C138 158 156 142 156 118 C156 82 100 28 100 28 Z" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<path d="M94 150 H106 V176 H94 Z" fill="#fff" opacity="0.85"/>',
    grad('g','#3A3A50','#1A1A28'))

S2['note'] = svg(
    '<rect x="42" y="30" width="116" height="146" rx="12" fill="url(#g)" stroke="#fff" stroke-width="5"/>'
    '<path d="M64 72 H136 M64 100 H136 M64 128 H112" stroke="#fff" stroke-width="7" stroke-linecap="round" opacity="0.85"/>',
    grad('g','#FFE07A','#FFB347'))

print(f"svg set2: {0} file siap di tulis")
for k,v in S2.items():
    open(f'stickers/set2/{k}.svg','w').write(v)
print(f"{len(S2)} svg dibuat")
