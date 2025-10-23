PRAGMA foreign_keys = ON;

-- tabel menu
CREATE TABLE IF NOT EXISTS menu (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    name TEXT NOT NULL,
                                    description TEXT,
                                    category TEXT,
                                    price INTEGER NOT NULL,
                                    image_path TEXT
);

-- tabel pesanan (order)
CREATE TABLE IF NOT EXISTS pesanan (
                                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       kode TEXT NOT NULL,
                                       total INTEGER NOT NULL,
                                       catatan TEXT,
                                       status TEXT,
                                       waktu TEXT
);

-- tabel item order
CREATE TABLE IF NOT EXISTS pesanan_item (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                                            pesanan_id INTEGER NOT NULL,
                                            menu_id INTEGER NOT NULL,
                                            nama TEXT,
                                            harga INTEGER,
                                            qty INTEGER,
                                            subtotal INTEGER,
                                            FOREIGN KEY(pesanan_id) REFERENCES pesanan(id) ON DELETE CASCADE,
    FOREIGN KEY(menu_id) REFERENCES menu(id)
    );

-- sample seed data (menu) — edit harga / deskripsi sesuai kebutuhan
INSERT INTO menu(name, description, category, filter, price, image_path) VALUES
                                                                     ('Paket 1', 'Paket hemat berisi Bakmi Ayam, Pangsit Goreng, dan Es Teh Manis.', 'Promo', 'Paket', 35000, '/Images/paket1.png'),
                                                                     ('Paket 2', 'Paket lengkap dengan Bakmi Chili Oil, Dimsum, dan Es Jeruk.', 'Promo', 'Paket', 42000, '/Images/paket2.png'),
                                                                     ('Paket 3', 'Paket lengkap dengan Bakmi Pedas Mercon, Tahu Bakso, dan Es Lemon Soda.', 'Promo', 'Paket', 48000, '/Images/paket3.png'),
                                                                     ('Paket 4', 'Paket lengkap dengan Bakmi Ayam Panggang, Bakso Kuah, dan Es Teh Leci.', 'Promo', 'Paket', 50000, '/Images/paket4.png'),
                                                                     ('Paket 5', 'Paket lengkap dengan Nasi Goreng Katsu, Lumpia Ayam, dan Es Teh Manis.', 'Promo', 'Paket', 43000, '/Images/paket5.png'),
                                                                     ('Paket 6', 'Paket lengkap dengan Pangsit Chili Oil dan Es Kopi Gula Aren.', 'Promo', 'Paket', 35000, '/Images/paket6.png'),
                                                                     ('Paket 7', 'Paket lengkap dengan Nasi Ayam Goreng Mentega, Bakso Ikan Bakar, dan Jus Naga.', 'Promo', 'Paket', 54000, '/Images/paket7.png'),
                                                                     ('Paket 8', 'Paket lengkap dengan Bakmi Pangsit Rebus, Tahu Bakso, dan Teh Pucuk.', 'Promo', 'Paket', 39000, '/Images/paket8.png'),

                                                                     ('Nasi Goreng Spesial', 'Nasi goreng spesial dengan topping ayam & sosis.', 'Makanan', 'Nasi', 25000, '/Images/nasi_goreng_spesial.png'),
                                                                     ('Nasi Ayam Panggang', 'Nasi dengan ayam panggang empuk dan saus khas.', 'Makanan', 'Nasi', 28000, '/Images/nasi_ayam_panggang.png'),
                                                                     ('Nasi Ayam Jamur', 'Nasi dengan ayam dan jamur.', 'Makanan', 'Nasi', 22000, '/Images/nasi_ayam_jamur.png'),
                                                                     ('Nasi Goreng Katsu', 'Nasi goreng dengan ayam katsu crispy.', 'Makanan', 'Nasi', 26000, '/Images/nasi_goreng_katsu.png'),
                                                                     ('Nasi Ayam Goreng Mentega', 'Nasi dengan ayam goreng mentega.', 'Makanan', 'Nasi', 27000, '/Images/nasi_ayam_goreng_mentega.png'),

                                                                     ('Bakmi Ayam Panggang', 'Bakmi gurih dengan topping ayam panggang.', 'Makanan', 'Bakmi Original', 28000, '/Images/bakmi_ayam_panggang.png'),
                                                                     ('Bakmi Pedas Mercon', 'Bakmi pedas khas.', 'Makanan', 'Bakmi Pedas', 23000, '/Images/bakmi_pedas.png'),
                                                                     ('Bakmi Ayam', 'Bakmi lengkap dengan topping ayam.', 'Makanan', 'Bakmi Original', 18000, '/Images/bakmi_ayam.png'),
                                                                     ('Bakmi Pangsit Rebus', 'Bakmi dengan pangsit rebus isi ayam.', 'Makanan', 'Bakmi Original', 22000, '/Images/bakmi_pangsit_rebus.png'),
                                                                     ('Bakmi Chili Oil', 'Bakmi khas dengan minyak cabai.', 'Makanan', 'Bakmi Pedas', 18000, '/Images/bakmi_chili.png'),

                                                                     ('Bihun Kuah Udang', 'Bihun kuah lengkap dengan udang.', 'Makanan', 'Lainnya', 28000, '/Images/bihun_kuah_udang.png'),

                                                                     ('Pangsit Goreng', 'Pangsit goreng renyah isi ayam.', 'Camilan', 'Pangsit', 15000, '/Images/pangsit_goreng.png'),
                                                                     ('Dimsum Ayam', 'Dimsum ayam lembut.', 'Camilan', 'Dimsum', 18000, '/Images/dimsum_ayam.png'),
                                                                     ('Tahu Bakso', 'Tahu bakso disajikan dengan cabai.', 'Camilan', 'Bakso', 21500, '/Images/tahu_bakso.png'),
                                                                     ('Lumpia Ayam', 'Lumpia ayam crispy.', 'Camilan', 'Lainnya', 24000, '/Images/lumpia_ayam.png'),
                                                                     ('Pangsit Chili Oil', 'Pangsit rebus siram chili oil.', 'Camilan', 'Lainnya', 18500, '/Images/pangsit_chili_oil.png'),

                                                                     ('Es Teh Manis', 'Es teh manis dingin.', 'Minuman', 'Dingin', 8000, '/Images/es_teh.png'),
                                                                     ('Iced Lychee Tea', 'Teh leci dingin.', 'Minuman', 'Dingin', 12000, '/Images/lychee_tea.png'),
                                                                     ('Es Jeruk', 'Jeruk segar perasan.', 'Minuman', 'Dingin', 10000, '/Images/es_jeruk.png'),
                                                                     ('Jus Naga', 'Jus buah naga segar.', 'Minuman', 'Jus', 15000, '/Images/dragon_juice.png'),
                                                                     ('Iced Lemon Soda', 'Lemon soda dingin.', 'Minuman', 'Dingin', 15000, '/Images/lemon_soda.png'),
                                                                     ('Hot Lemon Tea', 'Teh panas dgn lemon.', 'Minuman', 'Panas', 10000, '/Images/hot_lemon_tea.png'),
                                                                     ('Hot Green Tea', 'Teh hijau panas.', 'Minuman', 'Panas', 12000, '/Images/hot_green_tea.png'),
                                                                     ('Hot Black Coffee', 'Kopi hitam panas.', 'Minuman', 'Panas', 10000, '/Images/hot_black_coffee.png'),
                                                                     ('Es Kopi Susu', 'Es kopi dengan susu.', 'Minuman', 'Dingin', 12000, '/Images/es_kopi_susu.png'),
                                                                     ('Es Kopi Gula Aren', 'Es kopi gula aren.', 'Minuman', 'Dingin', 20000, '/Images/es_kopi_gula_aren.png'),

                                                                     ('Aqua', 'Air mineral botol.', 'Minuman', 'Lainnya', 4000, '/Images/aqua.png'),
                                                                     ('Fruit Tea', 'Teh rasa buah kemasan.', 'Minuman', 'Lainnya', 6000, '/Images/fruit_tea.png'),
                                                                     ('Teh Pucuk', 'Teh botol merk.', 'Minuman', 'Lainnya', 6000, '/Images/teh_pucuk.png');