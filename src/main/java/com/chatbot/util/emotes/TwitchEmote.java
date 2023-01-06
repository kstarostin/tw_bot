package com.chatbot.util.emotes;

import java.util.List;
import java.util.Map;

public class TwitchEmote extends AbstractEmote {

    public TwitchEmote(final String code) {
        super(code);
    }

    public interface Sets {
        List<TwitchEmote> HAPPY = List.of(SevenTVChannel.jvcrPog, SevenTVChannel.Okayge, SevenTVChannel.Basedge, SevenTVChannel.PepeChill, SevenTVChannel.Starege, SevenTVChannel.MmmHmm,
                SevenTVGlobal.FeelsOkayMan, SevenTVChannel.Zaebis, BTTVChannel.OkayChamp, SevenTVGlobal.Gayge, SevenTVGlobal.peepoHappy, SevenTVChannel.Clueless, BTTVGlobal.FeelsGoodMan,
                BTTVGlobal.FeelsAmazingMan, TwitchGlobal.SUBprise, TwitchGlobal.TriHard, TwitchGlobal.FourHead, TwitchGlobal.BloodTrail, TwitchGlobal.SmileyFace38);

        List<TwitchEmote> POG = List.of(SevenTVChannel.jvcrPog, SevenTVChannel.Pogey, SevenTVChannel.stalkPog, SevenTVChannel.SHTO, TwitchGlobal.PogChamp);

        List<TwitchEmote> COOL = List.of(SevenTVGlobal.EZ, SevenTVChannel.Basedge, BTTVGlobal.KappaCool, SevenTVChannel.KKool, SevenTVChannel.Siga, TwitchGlobal.GlitchCat,
                TwitchGlobal.CoolCat, TwitchGlobal.DatSheffy, TwitchGlobal.DxCat);

        List<TwitchEmote> LAUGH = List.of(SevenTVChannel.OMEGALUL, SevenTVChannel.maaaaan, SevenTVChannel.OMEGOATSE, SevenTVChannel.KEKW, SevenTVChannel.StreamerDoesntKnow,
                SevenTVChannel.RoflanEbalo, TwitchGlobal.LUL, TwitchGlobal.SUBprise, SevenTVChannel.aRolf, BTTVGlobal.tf);

        List<TwitchEmote> DANCE = List.of(SevenTVChannel.goblinPls, SevenTVChannel.pepeJAM, SevenTVChannel.BoneZone, SevenTVChannel.catJAM, SevenTVChannel.ratJAM,
                SevenTVChannel.peepoDJ, SevenTVChannel.slavPls2, SevenTVChannel.DDoomer, BTTVChannel.roflanTanec, SevenTVChannel.MmmHmm, SevenTVGlobal.forsenPls,
                SevenTVChannel.pepeGuitar, SevenTVGlobal.PartyParrot, SevenTVGlobal.PepePls, SevenTVGlobal.AlienDance, SevenTVGlobal.RareParrot);

        List<TwitchEmote> SAD = List.of(SevenTVChannel.jvcrSad, SevenTVChannel.Sadge, SevenTVChannel.FeelsRainMan, SevenTVChannel.TrollDespair, SevenTVChannel.KEKWait,
                BTTVChannel.NotOkayChamp, SevenTVChannel.Aware, SevenTVChannel.Despairge, SevenTVChannel.Siga, SevenTVChannel.sunboyDespair, SevenTVGlobal.peepoSad,
                SevenTVGlobal.FeelsStrongMan, TwitchGlobal.PoroSad, TwitchGlobal.BibleThump, TwitchGlobal.SmileyFace36);

        List<TwitchEmote> GREETING = List.of(SevenTVChannel.XyliWave, SevenTVChannel.ZdarovaZaebal, SevenTVChannel.KKomrade, SevenTVChannel.noxSorry, TwitchGlobal.FutureMan,
                TwitchGlobal.PotFriend, TwitchGlobal.KonCha, TwitchGlobal.VoHiYo, TwitchGlobal.HeyGuys);

        List<TwitchEmote> CONFUSION = List.of(SevenTVChannel.Okayeg, SevenTVChannel.KEKWait, SevenTVChannel.CheNaxyi, SevenTVChannel.XyliHuh, SevenTVChannel.Pausey,
                SevenTVGlobal.FeelsDankMan, SevenTVChannel.roflanUpalo, SevenTVChannel.FeelsSpecialMan, SevenTVChannel.XyliSnes, SevenTVChannel.deshovka, SevenTVChannel.xyliNado,
                SevenTVChannel.XyliChel, SevenTVGlobal.WAYTOODANK, SevenTVGlobal.Stare, SevenTVGlobal.FeelsWeirdMan, BTTVChannel.Ebaka, TwitchGlobal.RlyTho);

        List<TwitchEmote> SCARY = List.of(SevenTVChannel.jvcrS, SevenTVChannel.monkaStop, SevenTVChannel.monkaChrist, BTTVChannel.Ebaka, TwitchGlobal.WutFace);

        List<TwitchEmote> FAVOURITE = List.of(SevenTVChannel.jvcrPog, SevenTVChannel.jvcrEbalo, SevenTVChannel.jvcrS, SevenTVChannel.jvcrSad);

        List<List<TwitchEmote>> ALL_SETS = List.of(HAPPY, POG, COOL, LAUGH, DANCE, SAD, GREETING, CONFUSION, SCARY);

        Map<TwitchEmote, List<TwitchEmote>> EMOTE_COMBINATIONS = Map.of(
                SevenTVChannel.Okayeg, List.of(SevenTVGlobal.TeaTime, SevenTVGlobal.WineTime),
                SevenTVChannel.Pogey, List.of(SevenTVGlobal.Clap, SevenTVGlobal.nymnCorn),
                SevenTVGlobal.EZ, List.of(SevenTVGlobal.Clap),
                SevenTVChannel.Terpiloid, List.of(SevenTVChannel.VodkaTime),
                SevenTVChannel.peepoDJ, List.of(SevenTVChannel.xar2EDM, SevenTVGlobal.PartyParrot),
                SevenTVChannel.hoSway, List.of(SevenTVChannel.xar2EDM),
                SevenTVChannel.xar2EDM, List.of(SevenTVChannel.peepoDJ, SevenTVGlobal.PartyParrot),
                SevenTVChannel.MmmHmm, List.of(SevenTVGlobal.PianoTime, SevenTVGlobal.GuitarTime),
                SevenTVGlobal.PartyParrot, List.of(SevenTVChannel.peepoDJ, SevenTVChannel.xar2EDM));
    }

    interface TwitchGlobal {
        TwitchEmote BibleThump = new TwitchEmote("BibleThump");
        TwitchEmote Awwdible = new TwitchEmote("Awwdible");
        TwitchEmote Lechonk = new TwitchEmote("Lechonk");
        TwitchEmote Getcamped = new TwitchEmote("Getcamped");
        TwitchEmote SUBprise = new TwitchEmote("SUBprise");
        TwitchEmote FallHalp = new TwitchEmote("FallHalp");
        TwitchEmote FallCry = new TwitchEmote("FallCry");
        TwitchEmote FallWinning = new TwitchEmote("FallWinning");
        TwitchEmote MechaRobot = new TwitchEmote("MechaRobot");
        TwitchEmote ImTyping = new TwitchEmote("ImTyping");
        TwitchEmote Shush = new TwitchEmote("Shush");
        TwitchEmote MyAvatar = new TwitchEmote("MyAvatar");
        TwitchEmote PizzaTime = new TwitchEmote("PizzaTime");
        TwitchEmote LaundryBasket = new TwitchEmote("LaundryBasket");
        TwitchEmote ModLove = new TwitchEmote("ModLove");
        TwitchEmote PotFriend = new TwitchEmote("PotFriend");
        TwitchEmote Jebasted = new TwitchEmote("Jebasted");
        TwitchEmote PogBones = new TwitchEmote("PogBones");
        TwitchEmote PoroSad = new TwitchEmote("PoroSad");
        TwitchEmote KEKHeim = new TwitchEmote("KEKHeim");
        TwitchEmote CaitlynS = new TwitchEmote("CaitlynS");
        TwitchEmote HarleyWink = new TwitchEmote("HarleyWink");
        TwitchEmote WhySoSerious = new TwitchEmote("WhySoSerious");
        TwitchEmote DarkKnight = new TwitchEmote("DarkKnight");
        TwitchEmote FamilyMan = new TwitchEmote("FamilyMan");
        TwitchEmote RyuChamp = new TwitchEmote("RyuChamp");
        TwitchEmote HungryPaimon = new TwitchEmote("HungryPaimon");
        TwitchEmote TransgenderPride = new TwitchEmote("TransgenderPride");
        TwitchEmote PansexualPride = new TwitchEmote("PansexualPride");
        TwitchEmote NonbinaryPride = new TwitchEmote("NonbinaryPride");
        TwitchEmote LesbianPride = new TwitchEmote("LesbianPride");
        TwitchEmote IntersexPride = new TwitchEmote("IntersexPride");
        TwitchEmote GenderFluidPride = new TwitchEmote("GenderFluidPride");
        TwitchEmote GayPride = new TwitchEmote("GayPride");
        TwitchEmote BisexualPride = new TwitchEmote("BisexualPride");
        TwitchEmote AsexualPride = new TwitchEmote("AsexualPride");
        TwitchEmote NewRecord = new TwitchEmote("NewRecord");
        TwitchEmote PogChamp = new TwitchEmote("PogChamp");
        TwitchEmote GlitchNRG = new TwitchEmote("GlitchNRG");
        TwitchEmote GlitchLit = new TwitchEmote("GlitchLit");
        TwitchEmote StinkyGlitch = new TwitchEmote("StinkyGlitch");
        TwitchEmote MercyWing1 = new TwitchEmote("MercyWing1");
        TwitchEmote MercyWing2 = new TwitchEmote("MercyWing2");
        TwitchEmote PartyHat = new TwitchEmote("PartyHat");
        TwitchEmote EarthDay = new TwitchEmote("EarthDay");
        TwitchEmote PopCorn = new TwitchEmote("PopCorn");
        TwitchEmote FBtouchdown = new TwitchEmote("FBtouchdown");
        TwitchEmote TPFufun = new TwitchEmote("TPFufun");
        TwitchEmote TwitchVotes = new TwitchEmote("TwitchVotes");
        TwitchEmote DarkMode = new TwitchEmote("DarkMode");
        TwitchEmote HSWP = new TwitchEmote("HSWP");
        TwitchEmote HSCheers = new TwitchEmote("HSCheers");
        TwitchEmote PowerUpL = new TwitchEmote("PowerUpL");
        TwitchEmote PowerUpR = new TwitchEmote("PowerUpR");
        TwitchEmote LUL = new TwitchEmote("LUL");
        TwitchEmote EntropyWins = new TwitchEmote("EntropyWins");
        TwitchEmote TPcrunchyroll = new TwitchEmote("TPcrunchyroll");
        TwitchEmote TwitchUnity = new TwitchEmote("TwitchUnity");
        TwitchEmote Squid4 = new TwitchEmote("Squid4");
        TwitchEmote Squid3 = new TwitchEmote("Squid3");
        TwitchEmote Squid2 = new TwitchEmote("Squid2");
        TwitchEmote Squid1 = new TwitchEmote("Squid1");
        TwitchEmote CrreamAwk = new TwitchEmote("CrreamAwk");
        TwitchEmote CarlSmile = new TwitchEmote("CarlSmile");
        TwitchEmote TwitchLit = new TwitchEmote("TwitchLit");
        TwitchEmote TehePelo = new TwitchEmote("TehePelo");
        TwitchEmote TearGlove = new TwitchEmote("TearGlove");
        TwitchEmote SabaPing = new TwitchEmote("SabaPing");
        TwitchEmote PunOko = new TwitchEmote("PunOko");
        TwitchEmote KonCha = new TwitchEmote("KonCha");
        TwitchEmote Kappu = new TwitchEmote("Kappu");
        TwitchEmote InuyoFace = new TwitchEmote("InuyoFace");
        TwitchEmote BigPhish = new TwitchEmote("BigPhish");
        TwitchEmote BegWan = new TwitchEmote("BegWan");
        TwitchEmote ThankEgg = new TwitchEmote("ThankEgg");
        TwitchEmote MorphinTime = new TwitchEmote("MorphinTime");
        TwitchEmote TheIlluminati = new TwitchEmote("TheIlluminati");
        TwitchEmote TBAngel = new TwitchEmote("TBAngel");
        TwitchEmote MVGame = new TwitchEmote("MVGame");
        TwitchEmote NinjaGrumpy = new TwitchEmote("NinjaGrumpy");
        TwitchEmote PartyTime = new TwitchEmote("PartyTime");
        TwitchEmote RlyTho = new TwitchEmote("RlyTho");
        TwitchEmote UWot = new TwitchEmote("UWot");
        TwitchEmote YouDontSay = new TwitchEmote("YouDontSay");
        TwitchEmote KAPOW = new TwitchEmote("KAPOW");
        TwitchEmote ItsBoshyTime = new TwitchEmote("ItsBoshyTime");
        TwitchEmote CoolStoryBob = new TwitchEmote("CoolStoryBob");
        TwitchEmote TriHard = new TwitchEmote("TriHard");
        TwitchEmote SuperVinlin = new TwitchEmote("SuperVinlin");
        TwitchEmote FootGoal = new TwitchEmote("FootGoal");
        TwitchEmote FootYellow = new TwitchEmote("FootYellow");
        TwitchEmote FootBall = new TwitchEmote("FootBall");
        TwitchEmote BlackLivesMatter = new TwitchEmote("BlackLivesMatter");
        TwitchEmote ExtraLife = new TwitchEmote("ExtraLife");
        TwitchEmote VirtualHug = new TwitchEmote("VirtualHug");
        TwitchEmote SmileyFace = new TwitchEmote("R-)");
        TwitchEmote SmileyFace1 = new TwitchEmote("R)");
        TwitchEmote SmileyFace2 = new TwitchEmote(");-p");
        TwitchEmote SmileyFace3 = new TwitchEmote(");p");
        TwitchEmote SmileyFace4 = new TwitchEmote(");-P");
        TwitchEmote SmileyFace5 = new TwitchEmote(");P");
        TwitchEmote SmileyFace6 = new TwitchEmote(":-p");
        TwitchEmote SmileyFace7 = new TwitchEmote(":p");
        TwitchEmote SmileyFace8 = new TwitchEmote(":-P");
        TwitchEmote SmileyFace9 = new TwitchEmote(":P");
        TwitchEmote SmileyFace10 = new TwitchEmote(");-)");
        TwitchEmote SmileyFace11 = new TwitchEmote(");)");
        TwitchEmote SmileyFace12 = new TwitchEmote(":-\\");
        TwitchEmote SmileyFace13 = new TwitchEmote(":\\");
        TwitchEmote SmileyFace14 = new TwitchEmote(":-/");
        TwitchEmote SmileyFace15 = new TwitchEmote(":/");
        TwitchEmote Heart = new TwitchEmote("<3");
        TwitchEmote SmileyFace16 = new TwitchEmote(":-o");
        TwitchEmote SmileyFace17 = new TwitchEmote(":o");
        TwitchEmote SmileyFace18 = new TwitchEmote(":-O");
        TwitchEmote SmileyFace19 = new TwitchEmote(":O");
        TwitchEmote SmileyFace20 = new TwitchEmote("8-)");
        TwitchEmote SmileyFace21 = new TwitchEmote("B-)");
        TwitchEmote SmileyFace22 = new TwitchEmote("B)");
        TwitchEmote SmileyFace23 = new TwitchEmote("o_o");
        TwitchEmote SmileyFace24 = new TwitchEmote("o_O");
        TwitchEmote SmileyFace25 = new TwitchEmote("O_O");
        TwitchEmote SmileyFace26 = new TwitchEmote("O_o");
        TwitchEmote SmileyFace27 = new TwitchEmote(":-Z");
        TwitchEmote SmileyFace28 = new TwitchEmote(":Z");
        TwitchEmote SmileyFace29 = new TwitchEmote(":-z");
        TwitchEmote SmileyFace30 = new TwitchEmote(":z");
        TwitchEmote SmileyFace31 = new TwitchEmote(":-|");
        TwitchEmote SmileyFace32 = new TwitchEmote(":|");
        TwitchEmote SmileyFace33 = new TwitchEmote(">(");
        TwitchEmote SmileyFace34 = new TwitchEmote(":-D");
        TwitchEmote SmileyFace35 = new TwitchEmote(":D");
        TwitchEmote SmileyFace36 = new TwitchEmote(":-(");
        TwitchEmote SmileyFace37 = new TwitchEmote(":(");
        TwitchEmote SmileyFace38 = new TwitchEmote(":-)");
        TwitchEmote BOP = new TwitchEmote("BOP");
        TwitchEmote SingsNote = new TwitchEmote("SingsNote");
        TwitchEmote SingsMic = new TwitchEmote("SingsMic");
        TwitchEmote TwitchSings = new TwitchEmote("TwitchSings");
        TwitchEmote SoonerLater = new TwitchEmote("SoonerLater");
        TwitchEmote HolidayTree = new TwitchEmote("HolidayTree");
        TwitchEmote HolidaySanta = new TwitchEmote("HolidaySanta");
        TwitchEmote HolidayPresent = new TwitchEmote("HolidayPresent");
        TwitchEmote HolidayLog = new TwitchEmote("HolidayLog");
        TwitchEmote HolidayCookie = new TwitchEmote("HolidayCookie");
        TwitchEmote GunRun = new TwitchEmote("GunRun");
        TwitchEmote PixelBob = new TwitchEmote("PixelBob");
        TwitchEmote FBPenalty = new TwitchEmote("FBPenalty");
        TwitchEmote FBChallenge = new TwitchEmote("FBChallenge");
        TwitchEmote FBCatch = new TwitchEmote("FBCatch");
        TwitchEmote FBBlock = new TwitchEmote("FBBlock");
        TwitchEmote FBSpiral = new TwitchEmote("FBSpiral");
        TwitchEmote FBPass = new TwitchEmote("FBPass");
        TwitchEmote FBRun = new TwitchEmote("FBRun");
        TwitchEmote MaxLOL = new TwitchEmote("MaxLOL");
        TwitchEmote TwitchRPG = new TwitchEmote("TwitchRPG");
        TwitchEmote PinkMercy = new TwitchEmote("PinkMercy");
        TwitchEmote Poooound = new TwitchEmote("Poooound");
        TwitchEmote CurseLit = new TwitchEmote("CurseLit");
        TwitchEmote BatChest = new TwitchEmote("BatChest");
        TwitchEmote BrainSlug = new TwitchEmote("BrainSlug");
        TwitchEmote PrimeMe = new TwitchEmote("PrimeMe");
        TwitchEmote StrawBeary = new TwitchEmote("StrawBeary");
        TwitchEmote RaccAttack = new TwitchEmote("RaccAttack");
        TwitchEmote UncleNox = new TwitchEmote("UncleNox");
        TwitchEmote WTRuck = new TwitchEmote("WTRuck");
        TwitchEmote TooSpicy = new TwitchEmote("TooSpicy");
        TwitchEmote Jebaited = new TwitchEmote("Jebaited");
        TwitchEmote DogFace = new TwitchEmote("DogFace");
        TwitchEmote BlargNaut = new TwitchEmote("BlargNaut");
        TwitchEmote TakeNRG = new TwitchEmote("TakeNRG");
        TwitchEmote GivePLZ = new TwitchEmote("GivePLZ");
        TwitchEmote imGlitch = new TwitchEmote("imGlitch");
        TwitchEmote pastaThat = new TwitchEmote("pastaThat");
        TwitchEmote copyThis = new TwitchEmote("copyThis");
        TwitchEmote UnSane = new TwitchEmote("UnSane");
        TwitchEmote DatSheffy = new TwitchEmote("DatSheffy");
        TwitchEmote TheTarFu = new TwitchEmote("TheTarFu");
        TwitchEmote PicoMause = new TwitchEmote("PicoMause");
        TwitchEmote TinyFace = new TwitchEmote("TinyFace");
        TwitchEmote DxCat = new TwitchEmote("DxCat");
        TwitchEmote RuleFive = new TwitchEmote("RuleFive");
        TwitchEmote VoteNay = new TwitchEmote("VoteNay");
        TwitchEmote VoteYea = new TwitchEmote("VoteYea");
        TwitchEmote PJSugar = new TwitchEmote("PJSugar");
        TwitchEmote DoritosChip = new TwitchEmote("DoritosChip");
        TwitchEmote OpieOP = new TwitchEmote("OpieOP");
        TwitchEmote FutureMan = new TwitchEmote("FutureMan");
        TwitchEmote ChefFrank = new TwitchEmote("ChefFrank");
        TwitchEmote StinkyCheese = new TwitchEmote("StinkyCheese");
        TwitchEmote NomNom = new TwitchEmote("NomNom");
        TwitchEmote SmoocherZ = new TwitchEmote("SmoocherZ");
        TwitchEmote cmonBruh = new TwitchEmote("cmonBruh");
        TwitchEmote KappaWealth = new TwitchEmote("KappaWealth");
        TwitchEmote MikeHogu = new TwitchEmote("MikeHogu");
        TwitchEmote VoHiYo = new TwitchEmote("VoHiYo");
        TwitchEmote KomodoHype = new TwitchEmote("KomodoHype");
        TwitchEmote SeriousSloth = new TwitchEmote("SeriousSloth");
        TwitchEmote OSFrog = new TwitchEmote("OSFrog");
        TwitchEmote OhMyDog = new TwitchEmote("OhMyDog");
        TwitchEmote KappaClaus = new TwitchEmote("KappaClaus");
        TwitchEmote KappaRoss = new TwitchEmote("KappaRoss");
        TwitchEmote MingLee = new TwitchEmote("MingLee");
        TwitchEmote SeemsGood = new TwitchEmote("SeemsGood");
        TwitchEmote twitchRaid = new TwitchEmote("twitchRaid");
        TwitchEmote duDudu = new TwitchEmote("duDudu");
        TwitchEmote riPepperonis = new TwitchEmote("riPepperonis");
        TwitchEmote NotLikeThis = new TwitchEmote("NotLikeThis");
        TwitchEmote DendiFace = new TwitchEmote("DendiFace");
        TwitchEmote GlitchCat = new TwitchEmote("GlitchCat");
        TwitchEmote CoolCat = new TwitchEmote("CoolCat");
        TwitchEmote KappaPride = new TwitchEmote("KappaPride");
        TwitchEmote ShadyLulu = new TwitchEmote("ShadyLulu");
        TwitchEmote ArgieB8 = new TwitchEmote("ArgieB8");
        TwitchEmote CorgiDerp = new TwitchEmote("CorgiDerp");
        TwitchEmote PraiseIt = new TwitchEmote("PraiseIt");
        TwitchEmote TTours = new TwitchEmote("TTours");
        TwitchEmote mcaT = new TwitchEmote("mcaT");
        TwitchEmote NotATK = new TwitchEmote("NotATK");
        TwitchEmote HeyGuys = new TwitchEmote("HeyGuys");
        TwitchEmote Mau5 = new TwitchEmote("Mau5");
        TwitchEmote PRChase = new TwitchEmote("PRChase");
        TwitchEmote WutFace = new TwitchEmote("WutFace");
        TwitchEmote BuddhaBar = new TwitchEmote("BuddhaBar");
        TwitchEmote PermaSmug = new TwitchEmote("PermaSmug");
        TwitchEmote panicBasket = new TwitchEmote("panicBasket");
        TwitchEmote BabyRage = new TwitchEmote("BabyRage");
        TwitchEmote HassaanChop = new TwitchEmote("HassaanChop");
        TwitchEmote TheThing = new TwitchEmote("TheThing");
        TwitchEmote EleGiggle = new TwitchEmote("EleGiggle");
        TwitchEmote RitzMitz = new TwitchEmote("RitzMitz");
        TwitchEmote YouWHY = new TwitchEmote("YouWHY");
        TwitchEmote PipeHype = new TwitchEmote("PipeHype");
        TwitchEmote BrokeBack = new TwitchEmote("BrokeBack");
        TwitchEmote ANELE = new TwitchEmote("ANELE");
        TwitchEmote PanicVis = new TwitchEmote("PanicVis");
        TwitchEmote GrammarKing = new TwitchEmote("GrammarKing");
        TwitchEmote PeoplesChamp = new TwitchEmote("PeoplesChamp");
        TwitchEmote SoBayed = new TwitchEmote("SoBayed");
        TwitchEmote BigBrother = new TwitchEmote("BigBrother");
        TwitchEmote Keepo = new TwitchEmote("Keepo");
        TwitchEmote Kippa = new TwitchEmote("Kippa");
        TwitchEmote RalpherZ = new TwitchEmote("RalpherZ");
        TwitchEmote TF2John = new TwitchEmote("TF2John");
        TwitchEmote ThunBeast = new TwitchEmote("ThunBeast");
        TwitchEmote WholeWheat = new TwitchEmote("WholeWheat");
        TwitchEmote DAESuppy = new TwitchEmote("DAESuppy");
        TwitchEmote FailFish = new TwitchEmote("FailFish");
        TwitchEmote HotPokket = new TwitchEmote("HotPokket");
        TwitchEmote FourHead = new TwitchEmote("4Head");
        TwitchEmote ResidentSleeper = new TwitchEmote("ResidentSleeper");
        TwitchEmote FUNgineer = new TwitchEmote("FUNgineer");
        TwitchEmote PMSTwin = new TwitchEmote("PMSTwin");
        TwitchEmote ShazBotstix = new TwitchEmote("ShazBotstix");
        TwitchEmote AsianGlow = new TwitchEmote("AsianGlow");
        TwitchEmote DBstyle = new TwitchEmote("DBstyle");
        TwitchEmote BloodTrail = new TwitchEmote("BloodTrail");
        TwitchEmote OneHand = new TwitchEmote("OneHand");
        TwitchEmote FrankerZ = new TwitchEmote("FrankerZ");
        TwitchEmote SMOrc = new TwitchEmote("SMOrc");
        TwitchEmote ArsonNoSexy = new TwitchEmote("ArsonNoSexy");
        TwitchEmote PunchTrees = new TwitchEmote("PunchTrees");
        TwitchEmote SSSsss = new TwitchEmote("SSSsss");
        TwitchEmote Kreygasm = new TwitchEmote("Kreygasm");
        TwitchEmote KevinTurtle = new TwitchEmote("KevinTurtle");
        TwitchEmote PJSalt = new TwitchEmote("PJSalt");
        TwitchEmote SwiftRage = new TwitchEmote("SwiftRage");
        TwitchEmote DansGame = new TwitchEmote("DansGame");
        TwitchEmote GingerPower = new TwitchEmote("GingerPower");
        TwitchEmote BCWarrior = new TwitchEmote("BCWarrior");
        TwitchEmote MrDestructoid = new TwitchEmote("MrDestructoid");
        TwitchEmote JonCarnage = new TwitchEmote("JonCarnage");
        TwitchEmote Kappa = new TwitchEmote("Kappa");
        TwitchEmote RedCoat = new TwitchEmote("RedCoat");
        TwitchEmote TheRinger = new TwitchEmote("TheRinger");
        TwitchEmote StoneLightning = new TwitchEmote("StoneLightning");
        TwitchEmote OptimizePrime = new TwitchEmote("OptimizePrime");
        TwitchEmote JKanStyle = new TwitchEmote("JKanStyle");
        TwitchEmote SmileyFace39 = new TwitchEmote(":P");
        TwitchEmote SmileyFace40 = new TwitchEmote(");)");
        TwitchEmote SmileyFace41 = new TwitchEmote(":/");
        TwitchEmote SmileyFace42 = new TwitchEmote("<3");
        TwitchEmote SmileyFace43 = new TwitchEmote(":O");
        TwitchEmote SmileyFace44 = new TwitchEmote("B)");
        TwitchEmote SmileyFace45 = new TwitchEmote("O_o");
        TwitchEmote SmileyFace46 = new TwitchEmote(":|");
        TwitchEmote SmileyFace47 = new TwitchEmote(">(");
        TwitchEmote SmileyFace48 = new TwitchEmote(":D");
        TwitchEmote SmileyFace49 = new TwitchEmote(":(");
        TwitchEmote SmileyFace50 = new TwitchEmote(":)");
        TwitchEmote SmileyFace51 = new TwitchEmote(");P");
        TwitchEmote SmileyFace52 = new TwitchEmote("R)");
    }

    interface BTTVGlobal {
        TwitchEmote tf = new TwitchEmote(":tf:");
        TwitchEmote CiGrip = new TwitchEmote("CiGrip");
        TwitchEmote DatSauce = new TwitchEmote("DatSauce");
        TwitchEmote ForeverAlone = new TwitchEmote("ForeverAlone");
        TwitchEmote GabeN = new TwitchEmote("GabeN");
        TwitchEmote HailHelix = new TwitchEmote("HailHelix");
        TwitchEmote ShoopDaWhoop = new TwitchEmote("ShoopDaWhoop");
        TwitchEmote MandMjc = new TwitchEmote("M&Mjc");
        TwitchEmote bttvNice = new TwitchEmote("bttvNice");
        TwitchEmote TwaT = new TwitchEmote("TwaT");
        TwitchEmote WatChuSay = new TwitchEmote("WatChuSay");
        TwitchEmote tehPoleCat = new TwitchEmote("tehPoleCat");
        TwitchEmote AngelThump = new TwitchEmote("AngelThump");
        TwitchEmote TaxiBro = new TwitchEmote("TaxiBro");
        TwitchEmote BroBalt = new TwitchEmote("BroBalt");
        TwitchEmote CandianRage = new TwitchEmote("CandianRage");
        TwitchEmote Gasp = new TwitchEmote("D:");
        TwitchEmote VisLaud = new TwitchEmote("VisLaud");
        TwitchEmote KaRappa = new TwitchEmote("KaRappa");
        TwitchEmote FishMoley = new TwitchEmote("FishMoley");
        TwitchEmote Hhhehehe = new TwitchEmote("Hhhehehe");
        TwitchEmote KKona = new TwitchEmote("KKona");
        TwitchEmote PoleDoge = new TwitchEmote("PoleDoge");
        TwitchEmote sosGame = new TwitchEmote("sosGame");
        TwitchEmote CruW = new TwitchEmote("CruW");
        TwitchEmote RarePepe = new TwitchEmote("RarePepe");
        TwitchEmote haHAA = new TwitchEmote("haHAA");
        TwitchEmote FeelsBirthdayMan = new TwitchEmote("FeelsBirthdayMan");
        TwitchEmote RonSmug = new TwitchEmote("RonSmug");
        TwitchEmote KappaCool = new TwitchEmote("KappaCool");
        TwitchEmote FeelsBadMan = new TwitchEmote("FeelsBadMan");
        TwitchEmote bUrself = new TwitchEmote("bUrself");
        TwitchEmote ConcernDoge = new TwitchEmote("ConcernDoge");
        TwitchEmote FeelsGoodMan = new TwitchEmote("FeelsGoodMan");
        TwitchEmote FireSpeed = new TwitchEmote("FireSpeed");
        TwitchEmote NaM = new TwitchEmote("NaM");
        TwitchEmote SourPls = new TwitchEmote("SourPls");
        TwitchEmote FeelsSnowMan = new TwitchEmote("FeelsSnowMan");
        TwitchEmote FeelsSnowyMan = new TwitchEmote("FeelsSnowyMan");
        TwitchEmote LuL = new TwitchEmote("LuL");
        TwitchEmote SoSnowy = new TwitchEmote("SoSnowy");
        TwitchEmote SaltyCorn = new TwitchEmote("SaltyCorn");
        TwitchEmote monkaS = new TwitchEmote("monkaS");
        TwitchEmote VapeNation = new TwitchEmote("VapeNation");
        TwitchEmote ariW = new TwitchEmote("ariW");
        TwitchEmote notsquishY = new TwitchEmote("notsquishY");
        TwitchEmote FeelsAmazingMan = new TwitchEmote("FeelsAmazingMan");
        TwitchEmote DuckerZ = new TwitchEmote("DuckerZ");
        TwitchEmote SqShy = new TwitchEmote("SqShy");
        TwitchEmote Wowee = new TwitchEmote("Wowee");
        TwitchEmote WubTF = new TwitchEmote("WubTF");
        TwitchEmote cvR = new TwitchEmote("cvR");
        TwitchEmote cvL = new TwitchEmote("cvL");
        TwitchEmote cvHazmat = new TwitchEmote("cvHazmat");
        TwitchEmote cvMask = new TwitchEmote("cvMask");
        TwitchEmote DogChamp = new TwitchEmote("DogChamp");
    }

    interface BTTVChannel {
        TwitchEmote PepeLaugh = new TwitchEmote("PepeLaugh");
        TwitchEmote monkaX = new TwitchEmote("monkaX");
        TwitchEmote billyReady = new TwitchEmote("billyReady");
        TwitchEmote OkayChamp = new TwitchEmote("OkayChamp");
        TwitchEmote NotOkayChamp = new TwitchEmote("NotOkayChamp");
        TwitchEmote ADIX = new TwitchEmote("ADIX");
        TwitchEmote roflanTanec = new TwitchEmote("roflanTanec");
        TwitchEmote Ebaka = new TwitchEmote("Ebaka");
        TwitchEmote Freeman = new TwitchEmote("Freeman");
        TwitchEmote catNope = new TwitchEmote("catNope");
        TwitchEmote catYep = new TwitchEmote("catYep");
        TwitchEmote pepeTarkov = new TwitchEmote("pepeTarkov");
    }

    interface FFZGlobal {
        TwitchEmote YooHoo = new TwitchEmote("YooHoo");
        TwitchEmote ManChicken = new TwitchEmote("ManChicken");
        TwitchEmote BeanieHipster = new TwitchEmote("BeanieHipster");
        TwitchEmote CatBag = new TwitchEmote("CatBag");
        TwitchEmote ZreknarF = new TwitchEmote("ZreknarF");
        TwitchEmote LilZ = new TwitchEmote("LilZ");
        TwitchEmote LaterSooner = new TwitchEmote("LaterSooner");
        TwitchEmote BORT = new TwitchEmote("BORT");
    }

    interface SevenTVGlobal {
        TwitchEmote WineTime = new TwitchEmote("WineTime");
        TwitchEmote RainTime = new TwitchEmote("RainTime");
        TwitchEmote forsenPls = new TwitchEmote("forsenPls");
        TwitchEmote CrayonTime = new TwitchEmote("CrayonTime");
        TwitchEmote reckH = new TwitchEmote("reckH");
        TwitchEmote PartyParrot = new TwitchEmote("PartyParrot");
        TwitchEmote ApuApustaja = new TwitchEmote("ApuApustaja");
        TwitchEmote Gayge = new TwitchEmote("Gayge");
        TwitchEmote YEAHBUT7TV = new TwitchEmote("YEAHBUT7TV");
        TwitchEmote PepePls = new TwitchEmote("PepePls");
        TwitchEmote BillyApprove = new TwitchEmote("BillyApprove");
        TwitchEmote WAYTOODANK = new TwitchEmote("WAYTOODANK");
        TwitchEmote peepoHappy = new TwitchEmote("peepoHappy");
        TwitchEmote peepoSad = new TwitchEmote("peepoSad");
        TwitchEmote nymnCorn = new TwitchEmote("nymnCorn");
        TwitchEmote GuitarTime = new TwitchEmote("GuitarTime");
        TwitchEmote SteerR = new TwitchEmote("SteerR");
        TwitchEmote Clap = new TwitchEmote("Clap");
        TwitchEmote Clap2 = new TwitchEmote("Clap2");
        TwitchEmote PianoTime = new TwitchEmote("PianoTime");
        TwitchEmote knaDyppaHopeep = new TwitchEmote("knaDyppaHopeep");
        TwitchEmote RoxyPotato = new TwitchEmote("RoxyPotato");
        TwitchEmote AlienDance = new TwitchEmote("AlienDance");
        TwitchEmote AYAYA = new TwitchEmote("AYAYA");
        TwitchEmote TeaTime = new TwitchEmote("TeaTime");
        TwitchEmote BasedGod = new TwitchEmote("BasedGod");
        TwitchEmote RebeccaBlack = new TwitchEmote("RebeccaBlack");
        TwitchEmote FeelsDankMan = new TwitchEmote("FeelsDankMan");
        TwitchEmote FeelsOkayMan = new TwitchEmote("FeelsOkayMan");
        TwitchEmote PETPET = new TwitchEmote("PETPET");
        TwitchEmote Stare = new TwitchEmote("Stare");
        TwitchEmote gachiGASM = new TwitchEmote("gachiGASM");
        TwitchEmote FeelsStrongMan = new TwitchEmote("FeelsStrongMan");
        TwitchEmote RareParrot = new TwitchEmote("RareParrot");
        TwitchEmote EZ = new TwitchEmote("EZ");
        TwitchEmote FeelsWeirdMan = new TwitchEmote("FeelsWeirdMan");
        TwitchEmote gachiBASS = new TwitchEmote("gachiBASS");
        TwitchEmote ppL = new TwitchEmote("ppL");
        TwitchEmote SevenTV = new TwitchEmote("(7TV)");
        TwitchEmote CaneTime = new TwitchEmote("CaneTime");
        TwitchEmote EggnogTime = new TwitchEmote("EggnogTime");
    }

    interface SevenTVChannel {
        TwitchEmote Okayeg = new TwitchEmote("Okayeg");
        TwitchEmote Sadge = new TwitchEmote("Sadge");
        TwitchEmote FeelsRainMan = new TwitchEmote("FeelsRainMan");
        TwitchEmote TrollDespair = new TwitchEmote("TrollDespair");
        TwitchEmote goblinPls = new TwitchEmote("goblinPls");
        TwitchEmote KKomrade = new TwitchEmote("KKomrade");
        TwitchEmote maaaaan = new TwitchEmote("maaaaan");
        TwitchEmote pepeJAM = new TwitchEmote("pepeJAM");
        TwitchEmote OMEGALUL = new TwitchEmote("OMEGALUL");
        TwitchEmote forsenCoomer = new TwitchEmote("forsenCoomer");
        TwitchEmote XyliHuh = new TwitchEmote("XyliHuh");
        TwitchEmote KEKW = new TwitchEmote("KEKW");
        TwitchEmote Peace = new TwitchEmote("Peace");
        TwitchEmote AlienPls3 = new TwitchEmote("AlienPls3");
        TwitchEmote KKool = new TwitchEmote("KKool");
        TwitchEmote noxSorry = new TwitchEmote("noxSorry");
        TwitchEmote catJAM = new TwitchEmote("catJAM");
        TwitchEmote Pausey = new TwitchEmote("Pausey");
        TwitchEmote YOURMOM = new TwitchEmote("YOURMOM");
        TwitchEmote BOOBA = new TwitchEmote("BOOBA");
        TwitchEmote RoflanEbalo = new TwitchEmote("RoflanEbalo");
        TwitchEmote AGAKAKSKAGESH = new TwitchEmote("AGAKAKSKAGESH");
        TwitchEmote xar2EDM = new TwitchEmote("xar2EDM");
        TwitchEmote KEKWait = new TwitchEmote("KEKWait");
        TwitchEmote BoneZone = new TwitchEmote("BoneZone");
        TwitchEmote Pogey = new TwitchEmote("Pogey");
        TwitchEmote ratJAM = new TwitchEmote("ratJAM");
        TwitchEmote Clueless = new TwitchEmote("Clueless");
        TwitchEmote roflanUpalo = new TwitchEmote("roflanUpalo");
        TwitchEmote BoomTime = new TwitchEmote("BoomTime");
        TwitchEmote StreamerDoesntKnow = new TwitchEmote("StreamerDoesntKnow");
        TwitchEmote Basedge = new TwitchEmote("Basedge");
        TwitchEmote chickenWalk = new TwitchEmote("chickenWalk");
        TwitchEmote Starege = new TwitchEmote("Starege");
        TwitchEmote ThisIsFine = new TwitchEmote("ThisIsFine");
        TwitchEmote FeelsSpecialMan = new TwitchEmote("FeelsSpecialMan");
        TwitchEmote monkaStop = new TwitchEmote("monkaStop");
        TwitchEmote PeepoKnife = new TwitchEmote("PeepoKnife");
        TwitchEmote Okayge = new TwitchEmote("Okayge");
        TwitchEmote SCAMMED = new TwitchEmote("SCAMMED");
        TwitchEmote slavPlz = new TwitchEmote("slavPlz");
        TwitchEmote PepeSerious = new TwitchEmote("PepeSerious");
        TwitchEmote Durka = new TwitchEmote("Durka");
        TwitchEmote toddW = new TwitchEmote("toddW");
        TwitchEmote FeelsWowMan = new TwitchEmote("FeelsWowMan");
        TwitchEmote MmmHmm = new TwitchEmote("MmmHmm");
        TwitchEmote peepoDJ = new TwitchEmote("peepoDJ");
        TwitchEmote pepoG = new TwitchEmote("pepoG");
        TwitchEmote monkaChrist = new TwitchEmote("monkaChrist");
        TwitchEmote slavPls2 = new TwitchEmote("slavPls2");
        TwitchEmote Aware = new TwitchEmote("Aware");
        TwitchEmote sunboyCry = new TwitchEmote("sunboyCry");
        TwitchEmote HUH = new TwitchEmote("HUH");
        TwitchEmote Yasno = new TwitchEmote("Yasno");
        TwitchEmote EGuitarTime = new TwitchEmote("EGuitarTime");
        TwitchEmote hoSway = new TwitchEmote("hoSway");
        TwitchEmote XyliF = new TwitchEmote("XyliF");
        TwitchEmote XyliSnes = new TwitchEmote("XyliSnes");
        TwitchEmote NaSozvoneXyli = new TwitchEmote("NaSozvoneXyli");
        TwitchEmote Zaebis = new TwitchEmote("Zaebis");
        TwitchEmote CheNaxyi = new TwitchEmote("CheNaxyi");
        TwitchEmote Dopizdelsa = new TwitchEmote("Dopizdelsa");
        TwitchEmote ZdarovaZaebal = new TwitchEmote("ZdarovaZaebal");
        TwitchEmote XyiBudesh = new TwitchEmote("XyiBudesh");
        TwitchEmote chickenRun = new TwitchEmote("chickenRun");
        TwitchEmote DDoomer = new TwitchEmote("DDoomer");
        TwitchEmote boomerJAM = new TwitchEmote("boomerJAM");
        TwitchEmote Prayge = new TwitchEmote("Prayge");
        TwitchEmote Despairge = new TwitchEmote("Despairge");
        TwitchEmote deshovka = new TwitchEmote("deshovka");
        TwitchEmote rukopojatie = new TwitchEmote("rukopojatie");
        TwitchEmote xyliNado = new TwitchEmote("xyliNado");
        TwitchEmote XyliGun = new TwitchEmote("XyliGun");
        TwitchEmote ZZoomer = new TwitchEmote("ZZoomer");
        TwitchEmote Content = new TwitchEmote("Content");
        TwitchEmote OMEGOATSE = new TwitchEmote("OMEGOATSE");
        TwitchEmote bateYes = new TwitchEmote("bateYes");
        TwitchEmote arriveToLeave = new TwitchEmote("arriveToLeave");
        TwitchEmote XyliChel = new TwitchEmote("XyliChel");
        TwitchEmote freemanSpy1 = new TwitchEmote("freemanSpy1");
        TwitchEmote freemanSpy2 = new TwitchEmote("freemanSpy2");
        TwitchEmote freemanSpy3 = new TwitchEmote("freemanSpy3");
        TwitchEmote freemanWide = new TwitchEmote("freemanWide");
        TwitchEmote XerTebe = new TwitchEmote("XerTebe");
        TwitchEmote XyliBye = new TwitchEmote("XyliBye");
        TwitchEmote XyliWave = new TwitchEmote("XyliWave");
        TwitchEmote Siga = new TwitchEmote("Siga");
        TwitchEmote freemanTanec = new TwitchEmote("freemanTanec");
        TwitchEmote angryFreeman = new TwitchEmote("angryFreeman");
        TwitchEmote sonSobaki = new TwitchEmote("sonSobaki");
        TwitchEmote PoRukam = new TwitchEmote("PoRukam");
        TwitchEmote Terpiloid = new TwitchEmote("Terpiloid");
        TwitchEmote XyliTalk = new TwitchEmote("XyliTalk");
        TwitchEmote XyliPizdish = new TwitchEmote("XyliPizdish");
        TwitchEmote sunboyDespair = new TwitchEmote("sunboyDespair");
        TwitchEmote stalkPog = new TwitchEmote("stalkPog");
        TwitchEmote Liftgers = new TwitchEmote("Liftgers");
        TwitchEmote SHTO = new TwitchEmote("SHTO");
        TwitchEmote VodkaTime = new TwitchEmote("VodkaTime");
        TwitchEmote pepeGuitar = new TwitchEmote("pepeGuitar");
        TwitchEmote ViolinTime = new TwitchEmote("ViolinTime");
        TwitchEmote FluteTime = new TwitchEmote("FluteTime");
        TwitchEmote Sunboy_toad = new TwitchEmote("Sunboy_toad");
        TwitchEmote PepeChill = new TwitchEmote("PepeChill");
        TwitchEmote aRolf = new TwitchEmote("aRolf");
        TwitchEmote Kippah = new TwitchEmote("Kippah");
        TwitchEmote XyliVoyage = new TwitchEmote("XyliVoyage");
        TwitchEmote Shiza = new TwitchEmote("Shiza");
        TwitchEmote jvcrPog = new TwitchEmote("jvcrPog");
        TwitchEmote jvcrS = new TwitchEmote("jvcrS");
        TwitchEmote jvcrSad = new TwitchEmote("jvcrSad");
        TwitchEmote jvcrEbalo = new TwitchEmote("jvcrEbalo");
    }
}
