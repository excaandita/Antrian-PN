

 counter = 0;




function clickIE() {
           if (document.all) {
                      (message);
           return false;
           }
}

function clickNS(e) {
           if (document.layers||(document.getElementById&&!document.all)) {
                      if (e.which==2||e.which==3) {
                                 (message);return false;
                      }
           }
}

function roundToTwo(num) {
    return +(Math.round(num + "e+2")  + "e-2");
}

 function precise_round(num, decimals) {
    var t=Math.pow(10, decimals);
    return (Math.round((num * t) + (decimals>0?1:0)*(Math.sign(num) * (10 / Math.pow(100, decimals)))) / t).toFixed(decimals);
}

function numtocurr(a){
    if (a !== null) {
        a=a.toString();
        var b = a.replace(/[^\d\,]/g,'');
        var dump = b.split(',');
        var c = '';
        var lengthchar = dump[0].length;
        var j = 0;
        for (var i = lengthchar; i > 0; i--) {
                j = j + 1;
                if (((j % 3) == 1) && (j != 1)) {
                        c = dump[0].substr(i-1,1) + '.' + c;
                } else {
                        c = dump[0].substr(i-1,1) + c;
                }
        }

        if(dump.length>1){
                if(dump[1].length>0){
                        c += ','+dump[1];
                }else{
                        c += ',';
                }
        }
        return c;
    } else {
        return '0';
    }
}

function money_format(num) {
    if (num >= 0) {
        if(num.toString().indexOf('.') !== -1) {
            var coma = roundToTwo(num).toString().split('.');
            var new_coma = numtocurr(coma[0]);
            var new_coma2= '';
            if (coma[1] !== undefined && coma[1].length === 1) {
                new_coma2= coma[1]+'0';
            }
            else if (coma[1] === undefined) {
                new_coma2= '00';
            }
            else {
                new_coma2= coma[1];
            }
            if (new_coma2 === '00') {
                return new_coma;
            }else{
                var new_num  = new_coma+','+new_coma2;
                return new_num;
            }

        } else {
            return numtocurr(num);
        }
    }
    if (num < 0) {
        if(Math.abs(num).toString().indexOf('.') !== -1) {
            var coma = roundToTwo(num).toString().split('.');
            var new_coma = numtocurr(coma[0]);
            var new_coma2= '';

            if (((coma[1] !== undefined)?coma[1]:'0').length === 1) {
                new_coma2= ((coma[1] !== undefined)?coma[1]:'0')+'0';
            } else {
                new_coma2= coma[1];
            }
            var new_num  = new_coma+','+new_coma2;
            return '-'+new_num;
        } else {
            return '-'+numtocurr(num);
        }
    }
}

function money_format_save(value) {
    var str = value.toString().split('.').join('');
    var new_str = str.replace(/,/g, '.');
    return new_str;
}

function IsNumeric(input)
{
    return (input - 0) === input && (input+'').replace(/^\s+|\s+$/g, "").length > 0;
}
function strip(html) {
   var tmp = document.createElement("DIV");
   tmp.innerHTML = html;
   return tmp.textContent || tmp.innerText || "";
}
function pembulatan_seratus(num) {
    var angka = parseFloat(num);
    var kelipatan = 100;
    var sisa = angka % kelipatan;
    if (sisa !== 0) {
        var kekurangan = kelipatan - sisa;
        var hasilBulat = angka + kekurangan;
        return Math.ceil(hasilBulat);
    } else {
        return Math.ceil(angka);
    }
}

function pembulatan_custom(num, pembulat) {
    var angka = parseFloat(num);
    var kelipatan = pembulat;
    var sisa = angka % kelipatan;
    if (sisa !== 0) {
        var kekurangan = kelipatan - sisa;
        var hasilBulat = angka + kekurangan;
        return Math.ceil(hasilBulat);
    } else {
        return Math.ceil(angka);
    }
}

function pembulatan(bilangan, pembulat, pembulatan_keatas) {
    // pembulatan keatas merupakan variabel dengan tipe data boolean
    var angka = parseFloat(bilangan);
    var sisa = angka % pembulat;
    if (sisa !== 0) {
        if (pembulatan_keatas) {
            var kekurangan = pembulat - sisa;
            var hasilBulat = angka + kekurangan;
            return Math.ceil(hasilBulat);
        } else {
            var hasilBulat = angka - sisa;
            return Math.ceil(hasilBulat);
        }
    } else {
        return Math.ceil(angka);
    }
}

function round_250(angka) { // asumsi variabel angka adalah numerik bukan currency yang berkoma dua
    var a   = String(angka);
    var b   = a.length;
    var result= angka;
    if (b > 2) {
        var value = a.substr((b-3), 3);
        if (value < 250) {
            var init = a.substr(0, (b-3));
            result = init+'000';
        }
        if (value > 250 && value < 500) {
            var init = a.substr(0, (b-3));
            result = parseFloat(init+'500');
        }
        if (value > 500 && value < 750) {
            var init = a.substr(0, (b-3));
            result = parseFloat(init+'500');
        }
        if (value > 750) {
            var init = a.substr(0, (b-3));
            result = parseFloat(init+'000') + 1000;
        }
    }
    return result;
}

function round(value, precision, mode) {
  //  discuss at: http://phpjs.org/functions/round/
  // original by: Philip Peterson
  //  revised by: Onno Marsman
  //  revised by: T.Wild
  //  revised by: Rafał Kukawski (http://blog.kukawski.pl/)
  //    input by: Greenseed
  //    input by: meo
  //    input by: William
  //    input by: Josep Sanz (http://www.ws3.es/)
  // bugfixed by: Brett Zamir (http://brett-zamir.me)
  //        note: Great work. Ideas for improvement:
  //        note: - code more compliant with developer guidelines
  //        note: - for implementing PHP constant arguments look at
  //        note: the pathinfo() function, it offers the greatest
  //        note: flexibility & compatibility possible
  //   example 1: round(1241757, -3);
  //   returns 1: 1242000
  //   example 2: round(3.6);
  //   returns 2: 4
  //   example 3: round(2.835, 2);
  //   returns 3: 2.84
  //   example 4: round(1.1749999999999, 2);
  //   returns 4: 1.17
  //   example 5: round(58551.799999999996, 2);
  //   returns 5: 58551.8

  var m, f, isHalf, sgn; // helper variables
  precision |= 0; // making sure precision is integer
  m = Math.pow(10, precision);
  value *= m;
  sgn = (value > 0) | -(value < 0); // sign of the number
  isHalf = value % 1 === 0.5 * sgn;
  f = Math.floor(value);

  if (isHalf) {
    switch (mode) {
      case 'PHP_ROUND_HALF_DOWN':
        value = f + (sgn < 0); // rounds .5 toward zero
        break;
      case 'PHP_ROUND_HALF_EVEN':
        value = f + (f % 2 * sgn); // rouds .5 towards the next even integer
        break;
      case 'PHP_ROUND_HALF_ODD':
        value = f + !(f % 2); // rounds .5 towards the next odd integer
        break;
      default:
        value = f + (sgn > 0); // rounds .5 away from zero
    }
  }

  return (isHalf ? value : Math.round(value)) / m;
}

 //Format Nilai Uang
 function number_format(a, b, c, d) {
            a = Math.round(a * Math.pow(10, b)) / Math.pow(10, b);
            e = a + '';
            f = e.split('.');
            if (!f[0]) {
            f[0] = '0';
            }
            if (!f[1]) {
            f[1] = '';
            }
            if (f[1].length < b) {
            g = f[1];
            for (i=f[1].length + 1; i <= b; i++) {
            g += '0';
            }
            f[1] = g;
            }
            if(d != '' && f[0].length > 3) {
            h = f[0];
            f[0] = '';
            for(j = 3; j < h.length; j+=3) {
            i = h.slice(h.length - j,h.length - j + 3);
            f[0] = d + i +  f[0] + '';
            }
            j = h.substr(0, (h.length % 3 == 0) ? 3 : (h.length % 3));
            f[0] = j + f[0];
            }
            c = (b <= 0) ? '' : c;
            return f[0] + c + f[1];
}

function zeroPad(num, numZeros) {
    var n = Math.abs(num);
    var zeros = Math.max(0, numZeros - Math.floor(n).toString().length );
    var zeroString = Math.pow(10,zeros).toString().substr(1);
    if( num < 0 ) {
        zeroString = '-' + zeroString;
    }

    return zeroString+n;
}

function datefmysql(tanggal) {
    if (tanggal !== undefined && tanggal !== null && tanggal !== 'null') {
        var elem = tanggal.split('-');
        var tahun = elem[0];
        var bulan = elem[1];
        var hari  = elem[2];
        return hari+'/'+bulan+'/'+tahun;
    } else {
        return '';
    }
}

function date2mysql(tgl) {
    var tanggal=tgl;
    var elem = tanggal.split('/');
    var tahun = elem[2];
    var bulan = elem[1];
    var hari  = elem[0];
    return tahun+'-'+bulan+'-'+hari;
}

function month2mysql(waktu) {
    var tanggal=waktu;
    var elem = tanggal.split('/');
    var tahun = elem[1];
    var bulan = elem[0];
    return tahun+'-'+bulan;
}



function datetimefmysql(waktu) {
    if ((waktu !== undefined) & (waktu !== null) & (waktu !== '')) {
        var el = waktu.split(' ');
        var tgl= datefmysql(el[0]);
        var tm = el[1].split(':');
        return tgl+' '+tm[0]+':'+tm[1];
    } else {
        return '-';
    }

}

function datetime2date(waktu) {
    if (waktu !== null) {
        var el = waktu.split(' ');
        var tgl= datefmysql(el[0]);
        return tgl;
    } else {
        return '-';
    }

}

function datetime2mysql(waktu){
    var el = waktu.split(' ');
    var tgl= date2mysql(el[0]);
    var tm = el[1].split(':');
    return tgl+' '+tm[0]+':'+tm[1];
}

function Angka(obj) {
        a = obj.value;
        b = a.replace(/[^\d]/g,'');
        c = '';
        lengthchar = b.length;
        j = 0;
        for (i = lengthchar; i > 0; i--) {
                j = j + 1;
                if (((j % 3) == 1) && (j != 1)) {
                        c = b.substr(i-1,1) + '' + c;
                } else {
                        c = b.substr(i-1,1) + c;
                }
        }
        obj.value = c;
}

function KuduAngka(obj) {
    a = obj.value;
    b = a.replace(/[^\d]/g,'');
    if (b.charAt(0) === '0') {
        b = b.substring(1,b.length)
    };
    c = '';
    lengthchar = b.length;
    j = 0;
    for (i = lengthchar; i > 0; i--) {
            j = j + 1;
            if (((j % 3) == 1) && (j != 1)) {
                    c = b.substr(i-1,1) + '' + c;
            } else {
                    c = b.substr(i-1,1) + c;
            }
    }

    if (c === '') {
        c = 0;
    };
    obj.value = c;
}

function FormNum(obj) {
        var a = obj.value;
        /*b = a.replace(/[^\d]/g,'');
        c = '';
        lengthchar = b.length;
        j = 0;
        for (i = lengthchar; i > 0; i--) {
                j = j + 1;
                if (((j % 3) == 1) && (j != 1)) {
                        c = b.substr(i-1,1) + '.' + c;
                } else {
                        c = b.substr(i-1,1) + c;
                }
        }*/
        if (a !== '') {
            var c = money_format(currencyToNumber(a));
        } else {
            var c = '';
        }
        obj.value = c;
}

function deFormation(obj) {
        var a = obj.value;

        var c = '';
        if (a !== '') {
            c = money_format_save(a);
        }
        obj.value = c;
}

function toFormation(obj) {
        var a = obj.value;
        var b = a.split('.');
        var after_koma = b[1];
        if (parseFloat(after_koma) === 0 || after_koma === undefined) {
            after_koma = '00';
        }
        if (parseFloat(after_koma) > 0) {
            after_koma = b[1];
        }
        var c = '';
        if (a !== '') {
            c = numberToCurrency(currencyToNumber(b[0]))+','+after_koma;
        }
        obj.value = c;
}

function dePersenFormat(obj) {
    var a = obj.value;
    var result = a.replace(',','.');
    obj.value = result;
}

function persenFormat(obj) {
    var a = obj.value;
    var result = a.replace(',','.');
    obj.value = result;
}

function FormNum666(obj) {
        var a = obj.value;
        /*b = a.replace(/[^\d]/g,'');
        c = '';
        lengthchar = b.length;
        j = 0;
        for (i = lengthchar; i > 0; i--) {
                j = j + 1;
                if (((j % 3) == 1) && (j != 1)) {
                        c = b.substr(i-1,1) + '.' + c;
                } else {
                        c = b.substr(i-1,1) + c;
                }
        }*/
        if (a !== '') {
            var c = money_format(a);
        } else {
            var c = '';
        }
        obj.value = c;
}

function Desimal(obj){
    a=obj.value;
    var reg=new RegExp(/[0-9]+(?:\.[0-9]{0,2})?/g)
    b=a.match(reg,'');
    if(b==null){
        obj.value='';
    }else{
        obj.value=b[0];
    }

}

function titikKeKoma(obj){
    var a=obj.toString();
    var b='';
    if(a!=null){
        b=a.replace(/\./g,',');
    }
    return b;
}

function komaKeTitik(obj){
    var a=obj.toString();
    var b='';
    if(a!=null){
        b=a.replace(/\,/g,'.');
    }
    return b;
}

function numberToCurrency(a){
    //a = Math.round(a);
    if (a !== null) {
        a= a.toString();
        var b = a.replace(/[^\d\,]/g,'');
        var dump = b.split(',');
        var c = '';
        var lengthchar = dump[0].length;
        var j = 0;
        for (var i = lengthchar; i > 0; i--) {
                j = j + 1;
                if (((j % 3) == 1) && (j != 1)) {
                        c = dump[0].substr(i-1,1) + '.' + c;
                } else {
                        c = dump[0].substr(i-1,1) + c;
                }
        }

        if(dump.length>1){
                if(dump[1].length>0){
                        c += ','+dump[1];
                }else{
                        c += ',';
                }
        }
        return c;
    } else {
        return '0';
    }
}

function CurrNum(obj) {
    var nilai = obj.value;
    obj.value = currencyToNumber(nilai);
}

function currencyToNumber(a){
    var c = 0; var n = 0;
    if (a !== null && a !== undefined) {
        c=a.replace(/\.+/g, '');
        n= c.replace(/,/g, '.');
    }
    return parseFloat(n);
}

function formatNumber(obj) {
    var a = obj.value;
    obj.value = numberToCurrency(a);
}

function formatCurrNumber(obj) {
    var a = obj.value;
    obj.value = currencyToNumber(a);
}


function removeMe(el) {
    var parent = el.parentNode;
    parent.parentNode.removeChild(parent);
}


function removeHtmlTag(strx){
    if(strx.indexOf("<")!=-1) {
        var s = strx.split("<");
        for(var i=0;i<s.length;i++){
        if(s[i].indexOf(">")!=-1){
            s[i] = s[i].substring(s[i].indexOf(">")+1,s[i].length);
        }
    }
    strx = s.join(" ");
    }
    return strx;
}



function parseDate(str) {
  var m = str.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
  return (m) ? new Date(m[3], m[2]-1, m[1]) : null;
}

function isNama(str){
  var reg=/^[a-zA-Z ]+$/g;
  return reg.test(str);
}

function getCookies(c_name)
{
    var i,x,y,ARRcookies=document.cookie.split(";");
    for (i=0;i<ARRcookies.length;i++)
  {
      x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
      y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
      x=x.replace(/^\s+|\s+$/g,"");
      if (x==c_name)
        {
        return unescape(y);
        }
      }
}

function setCookies(c_name,value,exminutes)
{
    var exdate=new Date();
    exdate.setMinutes(exdate.getMinutes()+exminutes,0,0);
    var c_value=escape(value) + ((exminutes==null) ? "" : "; expires="+exdate.toUTCString());
    //alert(c_value+'-->'+exdate.getMinutes()+''+exminutes);
    document.cookie=c_name + "=" + c_value;
}

function checkEmpty(id, value, hasil) {
    if ($('#'+id).val() == '') {
        alert('Data '+value+' tidak boleh kosong !');
        $('#'+id).focus();
        hasil;
    }
}

function createCookie(name, value, days) {
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            var expires = "; expires=" + date.toGMTString();
        }
        else var expires = "";

        var fixedName = '<%= Request["formName"] %>';
        name = fixedName + name;

        document.cookie = name + "=" + value + expires + "; path=/";
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function eraseCookie(name) {
    createCookie(name, "", -1);
}

function ageByBirth(b){
    // format tgl lahir = Y-m-d
    var format;
    try{
        var elem = b.split('-');
        var tahun = elem[0];
        var bulan = elem[1];
        var hari  = elem[2];

        var now=new Date();
        var day =now.getUTCDate();
        var month =now.getUTCMonth()+1;
        var year =now.getYear()+1900;

        tahun=year-tahun;
        bulan=month-bulan;
        hari=day-hari;

        var jumlahHari;
        var bulanTemp=(month==1)?12:month-1;
        if(bulanTemp==1 || bulanTemp==3 || bulanTemp==5 || bulanTemp==7 || bulanTemp==8 || bulanTemp==10 || bulanTemp==12){
            jumlahHari=31;
        }else if(bulanTemp==2){
            if(tahun % 4==0)
                jumlahHari=29;
            else
                jumlahHari=28;
        }else{
            jumlahHari=30;
        }

        if(hari<=0){
            hari+=jumlahHari;
            bulan--;
        }
        if(bulan<0 || (bulan==0 && tahun!=0)){
            bulan+=12;
            tahun--;
        }

        format = tahun+" Tahun "+bulan+" Bulan "+hari+" Hari";
    }catch(err){
        format = "-";
    }
    return format;

}

function pagination(total_data, limit, page,tab){
    var str = '';
    var totalPage = Math.ceil(total_data/limit);

    var first = '<li class="page-item"><a class="page-link" onclick="paging(1,'+tab+')">First</a></li>';
    var last = '<li class="page-item"><a class="page-link" onclick="paging('+((totalPage===0)?1:totalPage)+','+tab+')">Last</a></li>';
    var click_prev = '';
    if (page > 1) {
        click_prev = 'onclick="paging('+(page - 1)+','+tab+')"';
    };
    var prev = '<li class="page-item"><a class="page-link" '+click_prev+'>&laquo;</a></li>';

    var click_next = '';
    if (page < totalPage) {
        click_next = 'onclick="paging('+(page + 1)+','+tab+')"';
    };
    var next = '<li class="page-item"><a class="page-link" class="page-link" '+click_next+'>&raquo;</a></li>';

    var page_numb = '';
    var act_click = '';
    var start = page - 2;
    var finish = page + 2;
    if (start < 1) {
        start = 1;
    }

    if (finish > totalPage){
        finish = totalPage;
    }

    for (var p = start; p <= finish; p++) {

        if (p !== page) {
            page_numb += '<li class="page-item"><a class="page-link" onclick="paging('+p+','+tab+')">'+p+'</a></li>';
        }else{
            page_numb += '<li class="page-item active"><a class="page-link" onclick="paging('+p+','+tab+')">'+p+'</a></li>';
            //page_numb += '<li class="active"><span><input min="1" onkeyup="KuduAngka(this)" type="number" value="'+page+'" style="width:60px;"/><button class="btn btn-default btn-xs" title="Lompat ke halaman" onclick="gotopage(this, '+tab+')"><i class="fa fa-search"></i></button></span></li>';
        }

    };



    return '<nav aria-label="..."><ul class="pagination pointer">'+first+prev+page_numb+next+last+'</ul></nav>';
}

function gotopage(obj, tab){
    var a = $(obj).prev().val();
    var b = parseInt(a);
    if (b === 0) {
        b = 1;
    };
    paging(b, tab);
}

function jump_page(jumlah, limit){
    return round(jumlah / limit);
}


function page_summary(total_data, total_datapage,limit, page){
    var start = ((page -1) * limit)+1;

    var finish = (start -1) + total_datapage;
    if (finish < 1) {
        start = 0;
    };
    var str = 'Menunjukkan '+start+' - '+finish+' dari '+total_data+' data';

    return str;
}

function my_ajax(url,element){
    $.ajax({
        url: url,
        dataType: '',
        success: function( response ) {
            $(element).html(response);
        },
        error: function(e){
            access_failed(e.status);
        }
    });
}

function dc_validation(element, pesan){
    $(element).next().remove();
    $(element).after('<div class="error" style="font-weight: normal; color: red;">'+pesan+'</div>').closest('.form-group').removeClass('has-success').addClass('has-error');
}

function dc_validation_remove(element){
    $(element).next().remove();
    $(element).closest('.form-group').removeClass('has-error');
}

function get_date_app(){
    var d =  new Date();
    var date = d.getDate();
    var month = d.getMonth();
    month++;

    if (month < 10) {
        month = '0'+String(month);
    };

    if(date < 10){
        date = '0'+String(date);
    }
    return date+'/'+month+'/'+d.getFullYear()+' '+d.getHours()+':'+d.getMinutes();
}

function indo_tgl(date){
    if (date.indexOf("-") > -1) {
        var buf = date.split('-');
    }else{
        var buf = date.split('/');
    }

    var bulan = ''
     switch (buf[1]) {
        case '01': bulan = 'Januari'; break;
        case '02': bulan = 'Februari'; break;
        case '03': bulan = 'Maret'; break;
        case '04': bulan = 'April'; break;
        case '05': bulan = 'Mei'; break;
        case '06': bulan = 'Juni'; break;
        case '07': bulan = 'Juli'; break;
        case '08': bulan = 'Agustus'; break;
        case '09': bulan = 'September'; break;
        case '10':bulan = 'Oktober'; break;
        case '11':bulan = 'November'; break;
        case '12':bulan = 'Desember'; break;

        default:
            break;
        }

     return buf[2]+" "+bulan+" "+buf[0];
}

function get_mont_format(date){
     var buf = date.split('/');
     var bulan = ''
     switch (buf[0]) {
        case '1': bulan = 'Januari'; break;
        case '2': bulan = 'Februari'; break;
        case '3': bulan = 'Maret'; break;
        case '4': bulan = 'April'; break;
        case '5': bulan = 'Mei'; break;
        case '6': bulan = 'Juni'; break;
        case '7': bulan = 'Juli'; break;
        case '8': bulan = 'Agustus'; break;
        case '9': bulan = 'September'; break;
        case '10':bulan = 'Oktober'; break;
        case '11':bulan = 'November'; break;
        case '12':bulan = 'Desember'; break;

        default:
            break;
        }

     return bulan+" "+buf[1];
}

function romanize(num) {
	if (!+num)
		return false;
	var	digits = String(+num).split(""),
		key = ["","C","CC","CCC","CD","D","DC","DCC","DCCC","CM",
		       "","X","XX","XXX","XL","L","LX","LXX","LXXX","XC",
		       "","I","II","III","IV","V","VI","VII","VIII","IX"],
		roman = "",
		i = 3;
	while (i--)
		roman = (key[+digits.pop() + (i * 10)] || "") + roman;
	return Array(+digits.join("") + 1).join("M") + roman;
}

function deromanize(str) {
	var	str = str.toUpperCase(),
		validator = /^M*(?:D?C{0,3}|C[MD])(?:L?X{0,3}|X[CL])(?:V?I{0,3}|I[XV])$/,
		token = /[MDLV]|C[MD]?|X[CL]?|I[XV]?/g,
		key = {M:1000,CM:900,D:500,CD:400,C:100,XC:90,L:50,XL:40,X:10,IX:9,V:5,IV:4,I:1},
		num = 0, m;
	if (!(str && validator.test(str)))
		return false;
	while (m = token.exec(str))
		num += key[m[0]];
	return num;
}

function DaysInMonth(Y, M) {
    with (new Date(Y, M, 1, 12)) {
        setDate(0);
        return getDate();
    }
}

function datediff(date1, date2) {
    var y1 = date1.getFullYear(), m1 = date1.getMonth(), d1 = date1.getDate(),
    y2 = date2.getFullYear(), m2 = date2.getMonth(), d2 = date2.getDate();
    if (d1 < d2) {
    m1--;
    d1 += (DaysInMonth(y2, m2) - 1);
    }
    if (m1 < m2) {
    y1--;
    m1 += 12;
    }

    return [y1 - y2, m1 - m2, d1 - d2];
}

function calculateAge(birthday) { // birthday is a date
   var ageDifMs = Date.now() - new Date(birthday);
   var ageDate = new Date(ageDifMs); // miliseconds from epoch
   return Math.abs(ageDate.getUTCFullYear() - 1970);
 }



function hitungUmur(datestring, delimiter){
  var x = datestring.split(delimiter);
  if (delimiter == "-"){
    // jika pemisahnya adalah "-" (format SQL)
    var newdatestr = x[0]+','+x[1]+','+x[2];
  }else{
    // jika pemisahnya adalah "/" (selain format SQL)
    var newdatestr = x[2]+','+x[1]+','+x[0];
  }
  birthTime = new Date(newdatestr)
  todaysTime = new Date();

/// Parse out specific date values
  todaysYear = todaysTime.getFullYear()
  todaysMonth = todaysTime.getMonth()
  todaysDate = todaysTime.getDate()
  todaysHour = todaysTime.getHours()
  todaysMinute = todaysTime.getMinutes()
  todaysSecond = todaysTime.getSeconds()
  birthYear = birthTime.getFullYear()
  birthMonth = birthTime.getMonth()
  birthDate = birthTime.getDate()
  birthHour = birthTime.getHours()
  birthMinute = birthTime.getMinutes()
  birthSecond = birthTime.getSeconds()

/// Adjusts for Leap Year Info
  if ((todaysYear / 4) == (Math.round(todaysYear / 4))) {
     countLeap = 29}
  else {
       countLeap = 28}

/// Calculate the days in the month
  if (todaysMonth == 2) {
     countMonth = countLeap}
  else {
       if (todaysMonth == 4) {
          countMonth = 30}
       else {
          if (todaysMonth == 6) {
             countMonth = 30}
          else {
             if (todaysMonth == 9) {
                countMonth = 30}
             else {
                if (todaysMonth == 11) {
                   countMonth = 30}
                else {
                   countMonth = 31}}}}}

/// Doing the subtactions
  if (todaysMinute > birthMinute) {
     diffMinute = todaysMinute - birthMinute
     calcHour = 0}
  else {
     diffMinute = todaysMinute + 60 - birthMinute
     calcHour = -1}
  if (todaysHour > birthHour) {
     diffHour = todaysHour - birthHour + calcHour
     calcDate = 0}
  else {
     diffHour = todaysHour + 24 - birthHour  + calcHour
     calcDate = -1}
  if (todaysDate > birthDate) {
     diffDate = todaysDate - birthDate + calcDate
     calcMonth = 0}
  else {
     diffDate = todaysDate + countMonth - birthDate  + calcDate
     calcMonth = -1}
  if (todaysMonth > birthMonth) {
     diffMonth = todaysMonth - birthMonth + calcMonth
     calcYear = 0}
  else {
     diffMonth = todaysMonth + 12 - birthMonth + calcMonth
     calcYear = -1}
  diffYear = todaysYear - birthYear + calcYear

/// Making sure it all adds up correctly
  if (diffMinute == 60) {
     diffMinute = 0
     diffHour = diffHour + 1}
  if (diffHour == 24) {
     diffHour = 0
     diffDate = diffDate + 1}
  if (diffDate == countMonth) {
     diffDate = 0
     diffMonth = diffMonth + 1}
  if (diffMonth == 12) {
     diffMonth = 0
     diffYear = diffYear + 1}

  return (diffYear + ' Tahun ' + diffMonth + ' Bulan ' +  diffDate + ' hari');
}

function ageBetweenDate(waktu) {
    // format tanggal yyyy-MM-dd H:i
    var side = waktu.split(' ');
    var elem = side[0].split('-');
    var time = side[1].split(':');

    var calday = elem[2];
    var calmon = elem[1];
    var calyear = elem[0];

    var dat = new Date();
    var curday = dat.getDate();
    var curmon = dat.getMonth()+1;
    var curyear = dat.getFullYear();

    if(curday == "" || curmon=="" || curyear=="" || calday=="" || calmon=="" || calyear==""){
        return "-";
    }else{
        var curd = new Date();
        var cald = new Date(calyear,calmon-1,calday, time[0], time[1], 0);
        var dife = datediff(curd,cald);
        return dife[0]+" Tahun "+dife[1]+" Bulan "+dife[2]+" Hari";

    }
}

function date_time(id, opsi) {
    date = new Date;
    year = date.getFullYear();
    month = date.getMonth();
    months = new Array('01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12');
    d = date.getDate();
    day = date.getDay();
    days = new Array('Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday');
    h = date.getHours();
    if(h<10) {
            h = "0"+h;
    }
    m = date.getMinutes();
    if(m<10) {
            m = "0"+m;
    }
    s = date.getSeconds();
    if(s<10) {
            s = "0"+s;
    }
    if (d < 10) {
        d = "0"+d;
    }
    result = d+'/'+months[month]+'/'+year+' '+h+':'+m+':'+s;
    if (opsi === 'html') {
        document.getElementById(id).innerHTML = result;
        setTimeout('date_time("'+id+'", "'+opsi+'");','1000');
    } else {
        document.getElementById(id).value = result;
        setTimeout('date_time("'+id+'", "'+opsi+'");','1000');
    }

    return true;
}

function precise_round(num, decimals) {
    var t=Math.pow(10, decimals);
    return (Math.round((num * t) + (decimals>0?1:0)*(Math.sign(num) * (10 / Math.pow(100, decimals)))) / t).toFixed(decimals);
}

function updateScroll(element_id){
    var element = document.getElementById(element_id);
    element.scrollTop = element.scrollHeight;
}

function datetimenow() {
    var now     = new Date();
    var year    = now.getFullYear();
    var month   = now.getMonth()+1;
    var day     = now.getDate();
    var hour    = now.getHours();
    var minute  = now.getMinutes();
    var second  = now.getSeconds();
    if(month.toString().length == 1) {
        var month = '0'+month;
    }
    if(day.toString().length == 1) {
        var day = '0'+day;
    }
    if(hour.toString().length == 1) {
        var hour = '0'+hour;
    }
    if(minute.toString().length == 1) {
        var minute = '0'+minute;
    }
    if(second.toString().length == 1) {
        var second = '0'+second;
    }
    var dateTime = year+'-'+month+'-'+day;
    return indo_tgl(dateTime)+' '+hour+':'+minute+':'+second;;
}

function string2datetime(waktu){
    var el = datetime2mysql(waktu).split(' ');
    var tgl = el[0]+"T"+el[1];
    return new Date(tgl + "Z");
}

// onkeypress input untuk memastikan user hanya mengetikkan angka
function isNumberKey(evt, from=null, to=null){
    var charCode = (evt.which) ? evt.which : evt.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57)){
        return false;
    }

    return true;
}




// replace null sesuai data yang diinginkan
function null_replace(val=null, replace=null){
    var hasil = val;
    if (val == null) {
        hasil = replace;
    }

    return hasil;
}

function datenow() {
    var now     = new Date();
    var year    = now.getFullYear();
    var month   = now.getMonth()+1;
    var day     = now.getDate();

    if(month.toString().length == 1) {
        var month = '0'+month;
    }
    if(day.toString().length == 1) {
        var day = '0'+day;
    }
    var dateTime = year+'-'+month+'-'+day;
    return dateTime;
}

function setSwitchery(switchElement, checkedBool) {
    if((checkedBool && !switchElement.isChecked()) || (!checkedBool && switchElement.isChecked())) {
        switchElement.setPosition(true);
        switchElement.handleOnchange(true);
    }
}

function numbList(i, page, limit) {
    return ((i+1) + ((page - 1) * limit));
}

function setSelect2(elem, value, text) {
    $(elem).empty();
    $newOption = $("<option></option>").val(value).text(text);
    $(elem).append($newOption).trigger('change');
}

function remove_list(el) {
    var parent = el.parentNode.parentNode;
    parent.parentNode.removeChild(parent);
}


function check_all(){
    $(".check").each( function() {
        //$(this).attr("checked","checked");
        $(this).prop( "checked", true );
    });
}

function uncheck_all(){
    $(".check").each( function() {
        //$(this).removeAttr('checked');
        $(this).prop( "checked", false );
    });
}

function timemilisFormat(mil, withTime) {
    var d = new Date(mil);
    return formatDate(d, withTime);
}

function formatDate(date, withTime) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    var time = '';
    if (withTime) {
        time = zeroPad(d.getHours(), 2)+':'+zeroPad(d.getMinutes(), 2);
    }

    return [year, month, day].join('-')+' '+time;
}



function play_notif(mp3file) {

    var notif = new Pizzicato.Sound('/assets/sound/'+mp3file, function() {
        // Sound loaded!
        notif.play();
    });

    notif.on('end', function() {
        notif.disconnect();
    });
}

function guid() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
}


function camelize(str) {
      return str.split(' ')
      // get rid of any extra spaces using trim
      .map(a => a.trim())
      // Convert first char to upper case for each word
      .map(a => a[0].toUpperCase() + a.substring(1))
      // Join all the strings back together
      .join("")
}

function error_response(e) {
    if (e.status == 401) {
        const {metaData:{code}, response} = e.responseJSON
        var res = JSON.parse(e.responseText);

        Swal.fire('Error', res.metaData.message.join(", "), 'error').then((result) => {
          if(result){
//            location.href = '/logout'
            Swal.fire({
              title: 'Login',
              html: `<input type="text" id="relogin" class="swal2-input" placeholder="Username" onkeydown="relogin(this)" tabindex="0">
              <div class="password-container-relogin">
                <input type="password" placeholder="Password..." id="passwordRelogin" class="swal2-input" onkeydown="relogin(this)" tabindex="0">
                <i class="fa fa-eye-slash" id="eye" onclick="relogin_eye()"></i>
              </div>`,
              confirmButtonText: 'Sign in',
              focusConfirm: true,
              preConfirm: () => {
                const login = Swal.getPopup().querySelector('#relogin').value
                const password = Swal.getPopup().querySelector('#passwordRelogin').value

                if (!login || !password) {
                  Swal.showValidationMessage(`Username dan password harus diisi`)
                }else{
                    var json = {
                        'username' : login,
                        'password' : password,
                        'web_login' : true
                      }
                    return $.ajax({
                        type: 'POST',
                        url: `${code == 402 ? '/api/auth/generate_token_portal': '/api/auth/generate_token'}`,
                        contentType: 'application/json; charset=utf-8',
                        data: JSON.stringify(json),
                        cache: false,
                        dataType : 'json',
                        success: function(data) {

                          if(data.metaData.code === 200){
                            //location.href = "/"
                            Swal.fire(`Login berhasil!`, '', 'success')
                          }else{
                            // Err msg
                            //swal.fire('Error', data.metaData.message.join(', '), 'error');
                            Swal.showValidationMessage(`Username dan password tidak sesuai`);
                          }

                        },
                        error: function(e){
                          console.log(e)
                          // Swal.fire('Error', e.responseText, 'error');
                          Swal.showValidationMessage(`Username dan password tidak sesuai`);
                        }
                    });
                }
              }
            })
            .then((res) => {
              console.log(res)
            })
          }
      })
    } else{
        Swal.fire('Error', e.responseText, 'error');
    }
}

function distinct(value, index, self) {
  return self.indexOf(value) === index;
}
