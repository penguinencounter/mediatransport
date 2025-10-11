# `mediatransport` type reservations
mediatransport uses a single byte to represent types. thus, space is quite limited.

- `0x00` (illegal; should always be an invalid type)

## `0x01` - `0x0f`: Hex Casting
- **built-in**
- `0x01` is **string** from **moreiotas** for historical reasons
- `0x02` is **true**
- `0x03` is **false**
- `0x04` is **null**
- `0x05` is **double**
- `0x06` is **pattern**
- `0x07` is **vec3**
- `0x08` is **list**
- `0x09-0x0f` (reserved)

## `0x40-0x47`: MoreIotas
- **built-in**
- `0x40` is **matrix**
- `0x41-0x47` (reserved)

## `0x50-0x57`: Hexpose
- **built-in**
- `0x50` is **text**
- `0x51-0x57` (reserved)

## `0x63-0x6e`: Hexic
- **external**
- `0x63-0x6e` (reserved)

## `0xf0-0xf7`: external specials
- ask to reserve IDs
- This block is for data that isn't iotas

## `0xf8-0xfe`: builtin specials
- **built-in**
- `0xf8-0xfd` (reserved)
- `0xfe` is **server configuration**

## `0xff`: Hex Casting (continued)
- `0xff` is **garbage**
