from PIL import Image, ImageDraw, ImageFilter
import os, random, math

A = 'backgrounds'
os.makedirs(A, exist_ok=True)
W = H = 1400

def save(img, name, q=92):
    p = f'{A}/{name}.webp'
    img.save(p, 'WEBP', quality=q, method=6)
    print(f"  {name}.webp ({os.path.getsize(p)//1024} KB)")

def blob(im, cx, cy, rad, color, alpha):
    layer = Image.new('RGBA', im.size, (0,0,0,0))
    ImageDraw.Draw(layer).ellipse([cx-rad, cy-rad, cx+rad, cy+rad], fill=color+(alpha,))
    layer = layer.filter(ImageFilter.GaussianBlur(max(2, rad//2)))
    im.paste(layer, (0,0), layer)

meshes = {
 'mesh_lavender': [(14,12,26), [(160,130,255),(255,150,200),(120,110,230)]],
 'mesh_forest':   [(8,20,16),  [(40,160,110),(90,200,140),(20,90,70)]],
 'mesh_peach':    [(30,16,14), [(255,160,120),(255,210,170),(240,120,110)]],
 'mesh_midnight': [(6,8,20),   [(40,60,160),(80,40,140),(20,30,90)]],
 'mesh_rose':     [(28,10,18), [(230,110,140),(180,90,160),(120,50,100)]],
 'mesh_mint':     [(10,24,22), [(80,220,190),(140,240,210),(40,160,150)]],
 'mesh_gold':     [(26,20,8),  [(220,170,60),(255,220,130),(160,120,40)]],
 'mesh_steel':    [(14,16,22), [(120,140,170),(80,100,140),(50,60,90)]],
}
for name,(base,cols) in meshes.items():
    img = Image.new('RGB',(W,H),base)
    random.seed(abs(hash(name)) % 9999)
    positions = [(int(W*0.2),int(H*0.2)),(int(W*0.8),int(H*0.25)),(int(W*0.5),int(H*0.8)),(int(W*0.15),int(H*0.7))]
    for i,c in enumerate(cols):
        x,y = positions[i % len(positions)]
        blob(img, x, y, random.randint(380,560), c, random.randint(110,180))
    save(img, name)

def gradient_stops(stops, name):
    img = Image.new('RGB',(W,H))
    d = ImageDraw.Draw(img)
    for y in range(H):
        t = y/(H-1)
        seg = t*(len(stops)-1)
        i = min(int(seg), len(stops)-2)
        f = seg - i
        a,b = stops[i], stops[i+1]
        d.line([(0,y),(W,y)], fill=tuple(int(a[k]+(b[k]-a[k])*f) for k in range(3)))
    save(img, name)

gradient_stops([(10,8,24),(50,20,60),(120,40,80),(220,90,70),(255,180,110)], 'grad_dawn')
gradient_stops([(4,6,18),(12,30,60),(20,70,110),(100,160,190),(220,240,245)], 'grad_sky')
gradient_stops([(18,6,10),(60,10,30),(120,20,50),(190,50,60),(240,120,90)], 'grad_crimson')
gradient_stops([(6,14,10),(10,40,30),(20,80,60),(60,140,110),(180,230,200)], 'grad_jade')
gradient_stops([(24,18,8),(60,44,16),(110,84,30),(180,140,60),(240,215,150)], 'grad_honey')
gradient_stops([(8,8,14),(24,24,40),(46,46,72),(80,80,120),(150,150,190)], 'grad_slate')

def bokeh(base, palette, name, count=110, seed=0):
    img = Image.new('RGB',(W,H),base)
    random.seed(seed)
    for _ in range(count):
        cx,cy = random.randint(0,W), random.randint(0,H)
        rad = random.randint(28, 170)
        blob(img, cx, cy, rad, random.choice(palette), random.randint(18, 62))
    save(img, name)

bokeh((10,10,16), [(255,120,80),(255,190,110),(200,100,160)], 'bokeh_ember', seed=101)
bokeh((8,10,18), [(90,150,240),(120,190,255),(160,130,250)], 'bokeh_azure', seed=202)
bokeh((12,14,10), [(150,230,150),(110,200,140),(220,240,160)], 'bokeh_green', seed=303)
bokeh((16,10,18), [(240,130,190),(200,110,230),(255,170,200)], 'bokeh_rose', seed=404)

def grain(base, amp, name, seed=0):
    img = Image.new('RGB',(W,H),base)
    px = img.load()
    random.seed(seed)
    for y in range(H):
        for x in range(W):
            n = random.randint(-amp, amp)
            r,g,b = px[x,y]
            px[x,y] = (max(0,min(255,r+n)),max(0,min(255,g+n)),max(0,min(255,b+n)))
    save(img, name, q=85)

grain((22,20,26), 12, 'grain_warm', 11)
grain((16,20,26), 12, 'grain_cool', 22)

def paper(name, base, fiber):
    img = Image.new('RGB',(W,H), base)
    px = img.load()
    random.seed(abs(hash(name))%9999)
    for y in range(H):
        for x in range(W):
            n = random.randint(-7,7)
            r,g,b = px[x,y]
            px[x,y] = (max(0,min(255,r+n)),max(0,min(255,g+n)),max(0,min(255,b+n)))
    d = ImageDraw.Draw(img)
    for _ in range(3000):
        x1,y1 = random.randint(0,W), random.randint(0,H)
        d.line([(x1,y1),(x1+random.randint(-80,80), y1+random.randint(-12,12))], fill=fiber, width=1)
    save(img, name)

paper('paper_white', (248,246,242), (238,235,228))
paper('paper_kraft', (206,180,140), (188,160,120))
paper('paper_black', (28,28,32), (38,38,44))

def pattern(name, draw_fn):
    img = Image.new('RGBA',(400,400),(0,0,0,0))
    d = ImageDraw.Draw(img)
    draw_fn(d)
    img.save(f'patterns/{name}.webp','WEBP',quality=92,method=6)
    print(f"  patterns/{name}.webp")

def p_hex(d):
    a = 28
    for row in range(-1,12):
        for col in range(-1,12):
            cx = col*44 + (22 if row%2 else 0)
            cy = row*38
            pts = [(cx+16*math.cos(math.radians(60*i)), cy+16*math.sin(math.radians(60*i))) for i in range(6)]
            d.polygon(pts, outline=(255,255,255,a))

def p_tri(d):
    a = 28
    for row in range(-1,14):
        for col in range(-1,14):
            x = col*40 + (20 if row%2 else 0)
            y = row*34
            d.polygon([(x,y+30),(x+20,y),(x+40,y+30)], outline=(255,255,255,a))

def p_grid(d):
    a = 28
    for x in range(0,400,32):
        d.line([(x,0),(x,400)], fill=(255,255,255,a), width=2)
    for y in range(0,400,32):
        d.line([(0,y),(400,y)], fill=(255,255,255,a), width=2)

def p_confetti(d):
    random.seed(77)
    for _ in range(90):
        x,y = random.randint(0,400), random.randint(0,400)
        w,h = random.randint(6,14), random.randint(2,5)
        d.rectangle([x,y,x+w,y+h], fill=(255,255,255,random.randint(40,90)))

def p_chevron(d):
    a = 30
    for row in range(0,420,34):
        d.line([(0,row),(100,row-22),(200,row),(300,row-22),(400,row)], fill=(255,255,255,a), width=4)

def p_stars(d):
    random.seed(88)
    for _ in range(70):
        x,y = random.randint(0,400), random.randint(0,400)
        r = random.randint(2,5)
        d.ellipse([x-r,y-r,x+r,y+r], fill=(255,255,255,random.randint(50,110)))

for n,f in [('hex',p_hex),('tri',p_tri),('grid',p_grid),('confetti',p_confetti),('chevron',p_chevron),('stars',p_stars)]:
    pattern(n,f)

print("\ndone")
