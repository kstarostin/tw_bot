package com.chatbot.util.emotes;

import java.util.List;

public interface BotEmote {
    interface Sets {
        List<String> HAPPY = List.of(SevenTVChannel.jvcrPog, SevenTVChannel.Okayge, SevenTVChannel.Basedge, SevenTVChannel.PepeChill, SevenTVChannel.Starege, SevenTVChannel.MmmHmm,
                SevenTVChannel.Zaebis, BTTVChannel.OkayChamp, SevenTVGlobal.Gayge, SevenTVGlobal.peepoHappy, SevenTVChannel.Clueless, SevenTVGlobal.FeelsOkayMan, BTTVGlobal.FeelsGoodMan,
                BTTVGlobal.FeelsAmazingMan, TwitchGlobal.SUBprise, TwitchGlobal.TriHard, TwitchGlobal.FourHead, TwitchGlobal.BloodTrail, TwitchGlobal.SmileyFace38);

        List<String> POG = List.of(SevenTVChannel.jvcrPog, SevenTVChannel.Pogey, SevenTVChannel.stalkPog, SevenTVChannel.SHTO, TwitchGlobal.PogChamp);

        List<String> COOL = List.of(SevenTVGlobal.EZ, SevenTVChannel.Basedge, BTTVGlobal.KappaCool, SevenTVChannel.KKool, SevenTVChannel.Siga, TwitchGlobal.GlitchCat,
                TwitchGlobal.CoolCat, TwitchGlobal.DatSheffy, TwitchGlobal.DxCat);

        List<String> LAUGH = List.of(SevenTVChannel.OMEGALUL, SevenTVChannel.maaaaan, SevenTVChannel.OMEGOATSE, SevenTVChannel.KEKW, SevenTVChannel.StreamerDoesntKnow,
                SevenTVChannel.RoflanEbalo, TwitchGlobal.LUL, TwitchGlobal.SUBprise, SevenTVChannel.aRolf, BTTVGlobal.tf);

        List<String> DANCE = List.of(SevenTVChannel.goblinPls, SevenTVChannel.pepeJAM, SevenTVChannel.BoneZone, SevenTVChannel.catJAM, SevenTVChannel.ratJAM,
                SevenTVChannel.peepoDJ, SevenTVChannel.slavPls2, SevenTVChannel.hoSway, SevenTVChannel.DDoomer, BTTVChannel.roflanTanec, SevenTVChannel.freemanTanec,
                SevenTVChannel.pepeGuitar, SevenTVGlobal.PartyParrot, SevenTVGlobal.PepePls, SevenTVGlobal.AlienDance, SevenTVGlobal.RareParrot);

        List<String> SAD = List.of(SevenTVChannel.jvcrSad, SevenTVChannel.Sadge, SevenTVChannel.FeelsRainMan, SevenTVChannel.TrollDespair, SevenTVChannel.KEKWait,
                BTTVChannel.NotOkayChamp, SevenTVChannel.Aware, SevenTVChannel.Despairge, SevenTVChannel.Siga, SevenTVChannel.sunboyDespair, SevenTVGlobal.peepoSad,
                SevenTVGlobal.FeelsStrongMan, TwitchGlobal.PoroSad, TwitchGlobal.BibleThump, TwitchGlobal.SmileyFace36);

        List<String> GREETING = List.of(SevenTVChannel.XyliWave, SevenTVChannel.ZdarovaZaebal, SevenTVChannel.KKomrade, SevenTVChannel.noxSorry, TwitchGlobal.FutureMan,
                TwitchGlobal.PotFriend, TwitchGlobal.KonCha, TwitchGlobal.VoHiYo, TwitchGlobal.HeyGuys);

        List<String> CONFUSION = List.of(SevenTVChannel.Okayeg, SevenTVChannel.KEKWait, SevenTVChannel.CheNaxyi, SevenTVChannel.XyliHuh, SevenTVChannel.Pausey,
                SevenTVChannel.roflanUpalo, SevenTVChannel.FeelsSpecialMan, SevenTVChannel.XyliSnes, SevenTVChannel.deshovka, SevenTVChannel.xyliNado, SevenTVChannel.XyliChel,
                SevenTVGlobal.WAYTOODANK, SevenTVGlobal.Stare, SevenTVGlobal.FeelsWeirdMan, BTTVChannel.Ebaka, TwitchGlobal.RlyTho);

        List<String> SCARY = List.of(SevenTVChannel.jvcrS, SevenTVChannel.monkaStop, SevenTVChannel.monkaChrist, BTTVChannel.Ebaka, TwitchGlobal.WutFace);

        List<String> FAVOURITE = List.of(SevenTVChannel.jvcrPog, SevenTVChannel.jvcrEbalo, SevenTVChannel.jvcrS, SevenTVChannel.jvcrSad);

        List<List<String>> ALL_SETS = List.of(HAPPY, POG, COOL, LAUGH, DANCE, SAD, GREETING, CONFUSION, SCARY, FAVOURITE);
    }

    interface TwitchGlobal {
        String BibleThump = "BibleThump";
        String Awwdible = "Awwdible";
        String Lechonk = "Lechonk";
        String Getcamped = "Getcamped";
        String SUBprise = "SUBprise";
        String FallHalp = "FallHalp";
        String FallCry = "FallCry";
        String FallWinning = "FallWinning";
        String MechaRobot = "MechaRobot";
        String ImTyping = "ImTyping";
        String Shush = "Shush";
        String MyAvatar = "MyAvatar";
        String PizzaTime = "PizzaTime";
        String LaundryBasket = "LaundryBasket";
        String ModLove = "ModLove";
        String PotFriend = "PotFriend";
        String Jebasted = "Jebasted";
        String PogBones = "PogBones";
        String PoroSad = "PoroSad";
        String KEKHeim = "KEKHeim";
        String CaitlynS = "CaitlynS";
        String HarleyWink = "HarleyWink";
        String WhySoSerious = "WhySoSerious";
        String DarkKnight = "DarkKnight";
        String FamilyMan = "FamilyMan";
        String RyuChamp = "RyuChamp";
        String HungryPaimon = "HungryPaimon";
        String TransgenderPride = "TransgenderPride";
        String PansexualPride = "PansexualPride";
        String NonbinaryPride = "NonbinaryPride";
        String LesbianPride = "LesbianPride";
        String IntersexPride = "IntersexPride";
        String GenderFluidPride = "GenderFluidPride";
        String GayPride = "GayPride";
        String BisexualPride = "BisexualPride";
        String AsexualPride = "AsexualPride";
        String NewRecord = "NewRecord";
        String PogChamp = "PogChamp";
        String GlitchNRG = "GlitchNRG";
        String GlitchLit = "GlitchLit";
        String StinkyGlitch = "StinkyGlitch";
        String MercyWing1 = "MercyWing1";
        String MercyWing2 = "MercyWing2";
        String PartyHat = "PartyHat";
        String EarthDay = "EarthDay";
        String PopCorn = "PopCorn";
        String FBtouchdown = "FBtouchdown";
        String TPFufun = "TPFufun";
        String TwitchVotes = "TwitchVotes";
        String DarkMode = "DarkMode";
        String HSWP = "HSWP";
        String HSCheers = "HSCheers";
        String PowerUpL = "PowerUpL";
        String PowerUpR = "PowerUpR";
        String LUL = "LUL";
        String EntropyWins = "EntropyWins";
        String TPcrunchyroll = "TPcrunchyroll";
        String TwitchUnity = "TwitchUnity";
        String Squid4 = "Squid4";
        String Squid3 = "Squid3";
        String Squid2 = "Squid2";
        String Squid1 = "Squid1";
        String CrreamAwk = "CrreamAwk";
        String CarlSmile = "CarlSmile";
        String TwitchLit = "TwitchLit";
        String TehePelo = "TehePelo";
        String TearGlove = "TearGlove";
        String SabaPing = "SabaPing";
        String PunOko = "PunOko";
        String KonCha = "KonCha";
        String Kappu = "Kappu";
        String InuyoFace = "InuyoFace";
        String BigPhish = "BigPhish";
        String BegWan = "BegWan";
        String ThankEgg = "ThankEgg";
        String MorphinTime = "MorphinTime";
        String TheIlluminati = "TheIlluminati";
        String TBAngel = "TBAngel";
        String MVGame = "MVGame";
        String NinjaGrumpy = "NinjaGrumpy";
        String PartyTime = "PartyTime";
        String RlyTho = "RlyTho";
        String UWot = "UWot";
        String YouDontSay = "YouDontSay";
        String KAPOW = "KAPOW";
        String ItsBoshyTime = "ItsBoshyTime";
        String CoolStoryBob = "CoolStoryBob";
        String TriHard = "TriHard";
        String SuperVinlin = "SuperVinlin";
        String FootGoal = "FootGoal";
        String FootYellow = "FootYellow";
        String FootBall = "FootBall";
        String BlackLivesMatter = "BlackLivesMatter";
        String ExtraLife = "ExtraLife";
        String VirtualHug = "VirtualHug";
        String SmileyFace = "R-)";
        String SmileyFace1 = "R)";
        String SmileyFace2 = ";-p";
        String SmileyFace3 = ";p";
        String SmileyFace4 = ";-P";
        String SmileyFace5 = ";P";
        String SmileyFace6 = ":-p";
        String SmileyFace7 = ":p";
        String SmileyFace8 = ":-P";
        String SmileyFace9 = ":P";
        String SmileyFace10 = ";-)";
        String SmileyFace11 = ";)";
        String SmileyFace12 = ":-\\";
        String SmileyFace13 = ":\\";
        String SmileyFace14 = ":-/";
        String SmileyFace15 = ":/";
        String Heart = "<3";
        String SmileyFace16 = ":-o";
        String SmileyFace17 = ":o";
        String SmileyFace18 = ":-O";
        String SmileyFace19 = ":O";
        String SmileyFace20 = "8-)";
        String SmileyFace21 = "B-)";
        String SmileyFace22 = "B)";
        String SmileyFace23 = "o_o";
        String SmileyFace24 = "o_O";
        String SmileyFace25 = "O_O";
        String SmileyFace26 = "O_o";
        String SmileyFace27 = ":-Z";
        String SmileyFace28 = ":Z";
        String SmileyFace29 = ":-z";
        String SmileyFace30 = ":z";
        String SmileyFace31 = ":-|";
        String SmileyFace32 = ":|";
        String SmileyFace33 = ">(";
        String SmileyFace34 = ":-D";
        String SmileyFace35 = ":D";
        String SmileyFace36 = ":-(";
        String SmileyFace37 = ":(";
        String SmileyFace38 = ":-)";
        String BOP = "BOP";
        String SingsNote = "SingsNote";
        String SingsMic = "SingsMic";
        String TwitchSings = "TwitchSings";
        String SoonerLater = "SoonerLater";
        String HolidayTree = "HolidayTree";
        String HolidaySanta = "HolidaySanta";
        String HolidayPresent = "HolidayPresent";
        String HolidayLog = "HolidayLog";
        String HolidayCookie = "HolidayCookie";
        String GunRun = "GunRun";
        String PixelBob = "PixelBob";
        String FBPenalty = "FBPenalty";
        String FBChallenge = "FBChallenge";
        String FBCatch = "FBCatch";
        String FBBlock = "FBBlock";
        String FBSpiral = "FBSpiral";
        String FBPass = "FBPass";
        String FBRun = "FBRun";
        String MaxLOL = "MaxLOL";
        String TwitchRPG = "TwitchRPG";
        String PinkMercy = "PinkMercy";
        String Poooound = "Poooound";
        String CurseLit = "CurseLit";
        String BatChest = "BatChest";
        String BrainSlug = "BrainSlug";
        String PrimeMe = "PrimeMe";
        String StrawBeary = "StrawBeary";
        String RaccAttack = "RaccAttack";
        String UncleNox = "UncleNox";
        String WTRuck = "WTRuck";
        String TooSpicy = "TooSpicy";
        String Jebaited = "Jebaited";
        String DogFace = "DogFace";
        String BlargNaut = "BlargNaut";
        String TakeNRG = "TakeNRG";
        String GivePLZ = "GivePLZ";
        String imGlitch = "imGlitch";
        String pastaThat = "pastaThat";
        String copyThis = "copyThis";
        String UnSane = "UnSane";
        String DatSheffy = "DatSheffy";
        String TheTarFu = "TheTarFu";
        String PicoMause = "PicoMause";
        String TinyFace = "TinyFace";
        String DxCat = "DxCat";
        String RuleFive = "RuleFive";
        String VoteNay = "VoteNay";
        String VoteYea = "VoteYea";
        String PJSugar = "PJSugar";
        String DoritosChip = "DoritosChip";
        String OpieOP = "OpieOP";
        String FutureMan = "FutureMan";
        String ChefFrank = "ChefFrank";
        String StinkyCheese = "StinkyCheese";
        String NomNom = "NomNom";
        String SmoocherZ = "SmoocherZ";
        String cmonBruh = "cmonBruh";
        String KappaWealth = "KappaWealth";
        String MikeHogu = "MikeHogu";
        String VoHiYo = "VoHiYo";
        String KomodoHype = "KomodoHype";
        String SeriousSloth = "SeriousSloth";
        String OSFrog = "OSFrog";
        String OhMyDog = "OhMyDog";
        String KappaClaus = "KappaClaus";
        String KappaRoss = "KappaRoss";
        String MingLee = "MingLee";
        String SeemsGood = "SeemsGood";
        String twitchRaid = "twitchRaid";
        String duDudu = "duDudu";
        String riPepperonis = "riPepperonis";
        String NotLikeThis = "NotLikeThis";
        String DendiFace = "DendiFace";
        String GlitchCat = "GlitchCat";
        String CoolCat = "CoolCat";
        String KappaPride = "KappaPride";
        String ShadyLulu = "ShadyLulu";
        String ArgieB8 = "ArgieB8";
        String CorgiDerp = "CorgiDerp";
        String PraiseIt = "PraiseIt";
        String TTours = "TTours";
        String mcaT = "mcaT";
        String NotATK = "NotATK";
        String HeyGuys = "HeyGuys";
        String Mau5 = "Mau5";
        String PRChase = "PRChase";
        String WutFace = "WutFace";
        String BuddhaBar = "BuddhaBar";
        String PermaSmug = "PermaSmug";
        String panicBasket = "panicBasket";
        String BabyRage = "BabyRage";
        String HassaanChop = "HassaanChop";
        String TheThing = "TheThing";
        String EleGiggle = "EleGiggle";
        String RitzMitz = "RitzMitz";
        String YouWHY = "YouWHY";
        String PipeHype = "PipeHype";
        String BrokeBack = "BrokeBack";
        String ANELE = "ANELE";
        String PanicVis = "PanicVis";
        String GrammarKing = "GrammarKing";
        String PeoplesChamp = "PeoplesChamp";
        String SoBayed = "SoBayed";
        String BigBrother = "BigBrother";
        String Keepo = "Keepo";
        String Kippa = "Kippa";
        String RalpherZ = "RalpherZ";
        String TF2John = "TF2John";
        String ThunBeast = "ThunBeast";
        String WholeWheat = "WholeWheat";
        String DAESuppy = "DAESuppy";
        String FailFish = "FailFish";
        String HotPokket = "HotPokket";
        String FourHead = "4Head";
        String ResidentSleeper = "ResidentSleeper";
        String FUNgineer = "FUNgineer";
        String PMSTwin = "PMSTwin";
        String ShazBotstix = "ShazBotstix";
        String AsianGlow = "AsianGlow";
        String DBstyle = "DBstyle";
        String BloodTrail = "BloodTrail";
        String OneHand = "OneHand";
        String FrankerZ = "FrankerZ";
        String SMOrc = "SMOrc";
        String ArsonNoSexy = "ArsonNoSexy";
        String PunchTrees = "PunchTrees";
        String SSSsss = "SSSsss";
        String Kreygasm = "Kreygasm";
        String KevinTurtle = "KevinTurtle";
        String PJSalt = "PJSalt";
        String SwiftRage = "SwiftRage";
        String DansGame = "DansGame";
        String GingerPower = "GingerPower";
        String BCWarrior = "BCWarrior";
        String MrDestructoid = "MrDestructoid";
        String JonCarnage = "JonCarnage";
        String Kappa = "Kappa";
        String RedCoat = "RedCoat";
        String TheRinger = "TheRinger";
        String StoneLightning = "StoneLightning";
        String OptimizePrime = "OptimizePrime";
        String JKanStyle = "JKanStyle";
        String SmileyFace39 = ":P";
        String SmileyFace40 = ";)";
        String SmileyFace41 = ":/";
        String SmileyFace42 = "<3";
        String SmileyFace43 = ":O";
        String SmileyFace44 = "B)";
        String SmileyFace45 = "O_o";
        String SmileyFace46 = ":|";
        String SmileyFace47 = ">(";
        String SmileyFace48 = ":D";
        String SmileyFace49 = ":(";
        String SmileyFace50 = ":)";
        String SmileyFace51 = ";P";
        String SmileyFace52 = "R)";
    }

    interface BTTVGlobal {
        String tf = ":tf:";
        String CiGrip = "CiGrip";
        String DatSauce = "DatSauce";
        String ForeverAlone = "ForeverAlone";
        String GabeN = "GabeN";
        String HailHelix = "HailHelix";
        String ShoopDaWhoop = "ShoopDaWhoop";
        String MandMjc = "M&Mjc";
        String bttvNice = "bttvNice";
        String TwaT = "TwaT";
        String WatChuSay = "WatChuSay";
        String tehPoleCat = "tehPoleCat";
        String AngelThump = "AngelThump";
        String TaxiBro = "TaxiBro";
        String BroBalt = "BroBalt";
        String CandianRage = "CandianRage";
        String Gasp = "D:";
        String VisLaud = "VisLaud";
        String KaRappa = "KaRappa";
        String FishMoley = "FishMoley";
        String Hhhehehe = "Hhhehehe";
        String KKona = "KKona";
        String PoleDoge = "PoleDoge";
        String sosGame = "sosGame";
        String CruW = "CruW";
        String RarePepe = "RarePepe";
        String haHAA = "haHAA";
        String FeelsBirthdayMan = "FeelsBirthdayMan";
        String RonSmug = "RonSmug";
        String KappaCool = "KappaCool";
        String FeelsBadMan = "FeelsBadMan";
        String bUrself = "bUrself";
        String ConcernDoge = "ConcernDoge";
        String FeelsGoodMan = "FeelsGoodMan";
        String FireSpeed = "FireSpeed";
        String NaM = "NaM";
        String SourPls = "SourPls";
        String FeelsSnowMan = "FeelsSnowMan";
        String FeelsSnowyMan = "FeelsSnowyMan";
        String LuL = "LuL";
        String SoSnowy = "SoSnowy";
        String SaltyCorn = "SaltyCorn";
        String monkaS = "monkaS";
        String VapeNation = "VapeNation";
        String ariW = "ariW";
        String notsquishY = "notsquishY";
        String FeelsAmazingMan = "FeelsAmazingMan";
        String DuckerZ = "DuckerZ";
        String SqShy = "SqShy";
        String Wowee = "Wowee";
        String WubTF = "WubTF";
        String cvR = "cvR";
        String cvL = "cvL";
        String cvHazmat = "cvHazmat";
        String cvMask = "cvMask";
        String DogChamp = "DogChamp";
    }

    interface BTTVChannel {
        String PepeLaugh = "PepeLaugh";
        String monkaX = "monkaX";
        String billyReady = "billyReady";
        String OkayChamp = "OkayChamp";
        String NotOkayChamp = "NotOkayChamp";
        String ADIX = "ADIX";
        String roflanTanec = "roflanTanec";
        String Ebaka = "Ebaka";
        String Freeman = "Freeman";
        String catNope = "catNope";
        String catYep = "catYep";
        String pepeTarkov = "pepeTarkov";
    }

    interface FFZGlobal {
        String YooHoo = "YooHoo";
        String ManChicken = "ManChicken";
        String BeanieHipster = "BeanieHipster";
        String CatBag = "CatBag";
        String ZreknarF = "ZreknarF";
        String LilZ = "LilZ";
        String LaterSooner = "LaterSooner";
        String BORT = "BORT";
    }

    interface SevenTVGlobal {
        String WineTime = "WineTime";
        String RainTime = "RainTime";
        String forsenPls = "forsenPls";
        String CrayonTime = "CrayonTime";
        String reckH = "reckH";
        String PartyParrot = "PartyParrot";
        String ApuApustaja = "ApuApustaja";
        String Gayge = "Gayge";
        String YEAHBUT7TV = "YEAHBUT7TV";
        String PepePls = "PepePls";
        String BillyApprove = "BillyApprove";
        String WAYTOODANK = "WAYTOODANK";
        String peepoHappy = "peepoHappy";
        String peepoSad = "peepoSad";
        String nymnCorn = "nymnCorn";
        String GuitarTime = "GuitarTime";
        String SteerR = "SteerR";
        String Clap = "Clap";
        String Clap2 = "Clap2";
        String PianoTime = "PianoTime";
        String knaDyppaHopeep = "knaDyppaHopeep";
        String RoxyPotato = "RoxyPotato";
        String AlienDance = "AlienDance";
        String AYAYA = "AYAYA";
        String TeaTime = "TeaTime";
        String BasedGod = "BasedGod";
        String RebeccaBlack = "RebeccaBlack";
        String FeelsDankMan = "FeelsDankMan";
        String FeelsOkayMan = "FeelsOkayMan";
        String PETPET = "PETPET";
        String Stare = "Stare";
        String gachiGASM = "gachiGASM";
        String FeelsStrongMan = "FeelsStrongMan";
        String RareParrot = "RareParrot";
        String EZ = "EZ";
        String FeelsWeirdMan = "FeelsWeirdMan";
        String gachiBASS = "gachiBASS";
        String ppL = "ppL";
        String SevenTV = "(7TV)";
        String CaneTime = "CaneTime";
        String EggnogTime = "EggnogTime";
    }

    interface SevenTVChannel {
        String Okayeg = "Okayeg";
        String Sadge = "Sadge";
        String FeelsRainMan = "FeelsRainMan";
        String TrollDespair = "TrollDespair";
        String goblinPls = "goblinPls";
        String KKomrade = "KKomrade";
        String maaaaan = "maaaaan";
        String pepeJAM = "pepeJAM";
        String OMEGALUL = "OMEGALUL";
        String forsenCoomer = "forsenCoomer";
        String XyliHuh = "XyliHuh";
        String KEKW = "KEKW";
        String Peace = "Peace";
        String AlienPls3 = "AlienPls3";
        String KKool = "KKool";
        String noxSorry = "noxSorry";
        String catJAM = "catJAM";
        String Pausey = "Pausey";
        String YOURMOM = "YOURMOM";
        String BOOBA = "BOOBA";
        String RoflanEbalo = "RoflanEbalo";
        String AGAKAKSKAGESH = "AGAKAKSKAGESH";
        String xar2EDM = "xar2EDM";
        String KEKWait = "KEKWait";
        String BoneZone = "BoneZone";
        String Pogey = "Pogey";
        String ratJAM = "ratJAM";
        String Clueless = "Clueless";
        String roflanUpalo = "roflanUpalo";
        String BoomTime = "BoomTime";
        String StreamerDoesntKnow = "StreamerDoesntKnow";
        String Basedge = "Basedge";
        String chickenWalk = "chickenWalk";
        String Starege = "Starege";
        String ThisIsFine = "ThisIsFine";
        String FeelsSpecialMan = "FeelsSpecialMan";
        String monkaStop = "monkaStop";
        String PeepoKnife = "PeepoKnife";
        String Okayge = "Okayge";
        String SCAMMED = "SCAMMED";
        String slavPlz = "slavPlz";
        String PepeSerious = "PepeSerious";
        String Durka = "Durka";
        String toddW = "toddW";
        String FeelsWowMan = "FeelsWowMan";
        String MmmHmm = "MmmHmm";
        String peepoDJ = "peepoDJ";
        String pepoG = "pepoG";
        String monkaChrist = "monkaChrist";
        String slavPls2 = "slavPls2";
        String Aware = "Aware";
        String sunboyCry = "sunboyCry";
        String HUH = "HUH";
        String Yasno = "Yasno";
        String EGuitarTime = "EGuitarTime";
        String hoSway = "hoSway";
        String XyliF = "XyliF";
        String XyliSnes = "XyliSnes";
        String NaSozvoneXyli = "NaSozvoneXyli";
        String Zaebis = "Zaebis";
        String CheNaxyi = "CheNaxyi";
        String Dopizdelsa = "Dopizdelsa";
        String ZdarovaZaebal = "ZdarovaZaebal";
        String XyiBudesh = "XyiBudesh";
        String chickenRun = "chickenRun";
        String DDoomer = "DDoomer";
        String boomerJAM = "boomerJAM";
        String Prayge = "Prayge";
        String Despairge = "Despairge";
        String deshovka = "deshovka";
        String rukopojatie = "rukopojatie";
        String xyliNado = "xyliNado";
        String XyliGun = "XyliGun";
        String ZZoomer = "ZZoomer";
        String Content = "Content";
        String OMEGOATSE = "OMEGOATSE";
        String bateYes = "bateYes";
        String arriveToLeave = "arriveToLeave";
        String XyliChel = "XyliChel";
        String freemanSpy1 = "freemanSpy1";
        String freemanSpy2 = "freemanSpy2";
        String freemanSpy3 = "freemanSpy3";
        String freemanWide = "freemanWide";
        String XerTebe = "XerTebe";
        String XyliBye = "XyliBye";
        String XyliWave = "XyliWave";
        String Siga = "Siga";
        String freemanTanec = "freemanTanec";
        String angryFreeman = "angryFreeman";
        String sonSobaki = "sonSobaki";
        String PoRukam = "PoRukam";
        String Terpiloid = "Terpiloid";
        String XyliTalk = "XyliTalk";
        String XyliPizdish = "XyliPizdish";
        String sunboyDespair = "sunboyDespair";
        String stalkPog = "stalkPog";
        String Liftgers = "Liftgers";
        String SHTO = "SHTO";
        String VodkaTime = "VodkaTime";
        String pepeGuitar = "pepeGuitar";
        String ViolinTime = "ViolinTime";
        String FluteTime = "FluteTime";
        String Sunboy_toad = "Sunboy_toad";
        String PepeChill = "PepeChill";
        String aRolf = "aRolf";
        String Kippah = "Kippah";
        String XyliVoyage = "XyliVoyage";
        String Shiza = "Shiza";
        String jvcrPog = "jvcrPog";
        String jvcrS = "jvcrS";
        String jvcrSad = "jvcrSad";
        String jvcrEbalo = "jvcrEbalo";
    }
}
