FROM vizzionnaire/openjdk11:bullseye-slim

COPY start-tb-vc-executor.sh ${pkg.name}.deb /tmp/

RUN mkdir -p /home/vizzionnaire/.config/jgit \
    && chown -R ${pkg.user}:${pkg.user} /home/vizzionnaire \
    && chmod a+x /tmp/*.sh \
    && mv /tmp/start-tb-vc-executor.sh /usr/bin && \
    (yes | dpkg -i /tmp/${pkg.name}.deb) && \
    rm /tmp/${pkg.name}.deb && \
    (systemctl --no-reload disable --now ${pkg.name}.service > /dev/null 2>&1 || :) && \
    chmod 555 ${pkg.installFolder}/bin/${pkg.name}.jar

USER ${pkg.user}

CMD ["start-tb-vc-executor.sh"]
