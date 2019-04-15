import java.time.Instant

def sampleSize = Integer.parseInt(args[0])

def random = new Random()
def requests = ["/api/user", "/api/help", "/report"]

def startingSecond = Instant.now().getEpochSecond()
def hits = 0
println '"remotehost","rfc931","authuser","date","request","status","bytes"'
def hitsPerSecond = random.nextInt(16)
for (def i in 1..sampleSize) {
    def host = "${random.nextInt(255)}.${random.nextInt(255)}.${random.nextInt(255)}.${random.nextInt(255)}"
    if (hits <= hitsPerSecond) {
        hits++
    } else {
        hitsPerSecond = random.nextInt(16)
        hits = 0
        startingSecond += random.nextInt(4)
    }
    def date = startingSecond
    def request = requests[random.nextInt(requests.size())]
    def bytes = random.nextInt(9999)
    println "\"${host}\",\"-\",\"apache\",${date},\"GET ${request} HTTP/1.0\",200,${bytes}"
}
