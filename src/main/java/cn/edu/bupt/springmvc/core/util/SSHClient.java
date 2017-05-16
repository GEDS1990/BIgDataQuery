package cn.edu.bupt.springmvc.core.util;

import com.jcabi.ssh.SSH;
import com.jcabi.ssh.Shell;

import java.net.UnknownHostException;

/**
 * Created by limingkun on 2017/5/6.
 */
public enum SSHClient {

    PERMSSH;

    public Shell.Plain shell = null;
    private SSHClient() {
        try {
            shell = new Shell.Plain(
                    new SSH(
                            "master", 22,
                            "root", "-----BEGIN RSA PRIVATE KEY-----\n" +
                            "MIIEpAIBAAKCAQEA0MGneg+6nPeLik9i0eamTTx+9puYCC3ncBLOYXouO55jKfva\n" +
                            "nuuXOFQXLe6RKv3nJm4Qt+rU6LPicQeDiwQQah/zj8p9aIczpBjcLs3bsWqNL+kB\n" +
                            "2L1Mx1BeJd8HiW9m/Pw15jTgPrx7oP+Nu2u59U4WyABn2cd8pYAmCZFDLpylrEuq\n" +
                            "yEcsZ2eZnph81Sz+bCLfuLg9Tkita00/MPuDF38Ul9vE7WN4AjQbln0e8Oo2x02P\n" +
                            "Agz0DOFSckXD/C58anoyE8SBY5xEtXjXzLCDXjXaJQLiWk92juNqx3I9UrIGsKw6\n" +
                            "Es8/yudbBNMKh0Cb4H5mwW10z87+2/aFNMjv2QIDAQABAoIBABeilU+gkERNUqMq\n" +
                            "McoKDzIXc1iAbUofyGBMAMnQh4OAf0G1nh74FXrvKmCs7gqsDIuxUntAPzLJiw25\n" +
                            "CA+pOiKJ042oI2K+S/58QhGahRElOy79dpQPeq3mT4KCKdxJnKNv+rpBqlHwDR9B\n" +
                            "T860d61qVfH2+csSZu5l/+h9ZdnU9iDmAS21rXqe7EtUQ+J/ruAvxDL5GayHRDIv\n" +
                            "feXPjvLxbIWpvHtbZE4lRius/7h6QtoRAGEEngEbtuxxXGn5nUr9GnrFpuop+peX\n" +
                            "kefHOhePDDmQ4Two7rM68R+sc7vWsDN0CQ9+NyWdo48IKT72vrzEUDmJhyBYu2vh\n" +
                            "ZcIYDHECgYEA7EJia7hA6WccTMk8db6ErzG218FSWYqjAlRtCqYWP5uT+WQX9eIK\n" +
                            "vADqKp7ZRhycCtSKNM9xvoE0HEn7LHoPULJ3COR0HcY53+Xa0V12XxLubKyAau2i\n" +
                            "eUExP0BJgumVjxxVURUaFjWylIvCDcGRmK+D7S4D0pkibKPOmubrUmMCgYEA4jL6\n" +
                            "+jUv96PREJXbkmDrVSq8eZachHv9ai0+UnHdyPyqH8my5epeINV8lA6T2/PXGJ3B\n" +
                            "v/naCQxgV2lIN1doehXF+U0eX3sg0K1RPhK5dMsIXdrejrYoZ9aKaYpHYsRjH99E\n" +
                            "rPmXrRuYppIOFGa8enHfqur8T3vR1zNve8eGK5MCgYAndrM/8QgOtKV6ggayWD+L\n" +
                            "kA9NnMInqMuvwPBx3epLd65Q8xI5hSdr3e8/bUc3ueGqwXPQfHIwcx6ENytYc48J\n" +
                            "WLYfayGmM41JQ/i4DheOq/fdeTb+JXGFNYmHWjKtRl0wCXiK0a6I1TFeNLYWXCX4\n" +
                            "ziCIRq5myCOC6B3DrBzUpwKBgQCTI8R80/JS7X4GguTk0CTlLrqRgTZpO9p5odqj\n" +
                            "eKxjnG/x45t60bzPMPArw1vNPU3TFnO9NILvVdypn7O5RCOTHuy8Ib07MPYEqGlr\n" +
                            "rnRzE8tqegBB5x5bhYRd6PRMvCEaz3p15poSohvnZqTAAqBiKgbrwJXGAm5axXuk\n" +
                            "kgKl/wKBgQDrq2UW3h0pynaouW4LGWZ1b2eevJctLvcoL2ZTUVEarc1kmjqZIwpL\n" +
                            "ETdPA452tV3+SDjb95QveZ1ZWJOZrWnJwxmJJUQW9uNiJdHF4CI0Xc5mDqvxO0W7\n" +
                            "DFZv7XdJwJNJtvlXFyPLh//Z1QwstYY2u6Z0H3aGOKjLYc283w1vZQ==\n" +
                            "-----END RSA PRIVATE KEY-----"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }
}
