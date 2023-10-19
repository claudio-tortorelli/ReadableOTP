# ReadableOTP

## OTP schema
### Rules score classification
Using up to 3 different digits rules
- 1 digits --> +2 Points
- 2 digits --> +1 Points
- 3 digits --> +0 Point
  
Including as facilities 
- symmetry (1) --> +1 Point (easy to read)
- repetitive patterns (2) --> +1 Point (easy to memorize)
- logical steps (3) --> +1 Point (easy to write)

### eight digit
#### two parts
- xxxx xxxx, 5555 5555, x=[0-9]

- xxxx yyyy, 1111 0000, x=[0-9], y!=x
- xyxy xyxy, 2121 2121, x=[0-9], y!=x
- xxyy xxyy, 0099 0099, x=[0-9], y!=x
- xxxx xxxy, 2222 2227, x=[0-9], y!=x
- xyyy yyyy, 4666 6666, x=[0-9], y!=x
- xxyy yyxx, 8822 2288, x=[0-9], y!=x

(+ subset)
- xxxx yyyy, 1111 2222, x=[0-8], y=x+1
- xyxy xyxy, 3434 3434, x=[0-8], y=x+1
- xxyy xxyy, 8899 8899, x=[0-8], y=x+1
- xxxx xxxy, 2222 2223, x=[0-8], y=x+1
- xyyy yyyy, 4555 5555, x=[0-8], y=x+1
- xxyy yyxx, 8899 9988, x=[0-8], y=x+1

- xyzz xyzz, 1322 1322, x=[0-9], y!=x, z=!y!=x
- xyyz xyyz, 4116 4116, x=[0-9], y!=x, z=!y!=x
- xyyy zzzz, 0999 3333, x=[0-9], y!=x, z=!y!=x

#### three parts?
- xx yyyy xx
- xy yyyy xy

### six digit

#### two parts
- xxx xxx, 111 111, x=[0-9]

- xxx yyy, 111 444, x=[0-9], y!=x
- xxy xxy, 004 004, x=[0-9], y!=x
- xyy xyy, 733 733, x=[0-9], y!=x
- xyx xyx, 414 414, x=[0-9], y!=x
- xxy yxx, 885 588, x=[0-9], y!=x
- xyy yyx, 766 667, x=[0-9], y!=x

- xyz xyz, 234 234, x=[0-7], y=x+1, z=y+1

#### three parts
- xx xx yy, 66 66 33, x=[0-9], y!=x  
- xx yy yy, 00 44 44, x=[0-9], y!=x
- xy xy xy, 93 93 93, x=[0-9], y!=x

- xx yy zz, 77 22 44, x=[0-9], y!=x, z=!y!=x
